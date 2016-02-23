package com.cisco.reader.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;

import com.cisco.reader.entity.Bookmark;
import com.cisco.reader.entity.Book;
import com.cisco.reader.entity.Highlight;
import com.cisco.reader.entity.Note;
import com.cisco.reader.entity.Page;
import com.cisco.reader.entity.User;
import com.cisco.reader.helpers.DOMBuilder;
import com.cisco.reader.helpers.predicates.IPredicate;
import com.cisco.reader.helpers.predicates.Predicate;
import com.cisco.reader.repository.BookmarkRepository;
import com.cisco.reader.repository.BookRepository;
import com.cisco.reader.repository.HighlightRepository;
import com.cisco.reader.repository.NoteRepository;
import com.cisco.reader.repository.PageRepository;
import com.cisco.reader.repository.SequenceCounterDao;
import com.cisco.reader.repository.UserRepository;
import com.google.common.base.CharMatcher;

@Controller
public class MigrationController {
	// paths
	private String basePath = "/Users/rajad/projects/online/books/";
	@Autowired
	private ClassPathXmlApplicationContext context;
	
	@Autowired BookRepository booksRepository;
	@Autowired UserRepository usersRepository;
	@Autowired PageRepository pagesRepository;
	@Autowired BookmarkRepository bookmarksRepository;
	@Autowired NoteRepository notesRepository;
	@Autowired HighlightRepository highlightsRepository;
	
	@Autowired
	SequenceCounterDao sequenceCounterDao;

	public MigrationController() {

	}

	public int size(Iterable<?> it) {
		if (it instanceof Collection)
			return ((Collection<?>) it).size();
		int i = 0;
		for (Iterator<?> iterator = it.iterator(); iterator.hasNext();) {
			iterator.next();
			i++;
		}
		return i;
	}

	private String getCurrentTimeWithFormat() {
		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(dt);
	}

	public boolean migrateAnnotations(String oldBookPath, String newBookPath, String userId) {

		Book oldBook = booksRepository.findOneByPath(oldBookPath);
		System.out.println("Old Book Info-->" + oldBook.toString());

		Book newBook = booksRepository.findOneByPath(newBookPath);
		System.out.println("New Book Info-->" + newBook.toString());

		String lang = oldBookPath.split("/")[1];

		User user = usersRepository.findOneByUid(userId);
		System.out.println("Users Info-->" + user.toString());

		this.migrate(oldBook, newBook, user, lang);
		return true;
	}

	private boolean migrate(Book oldBook, Book newBook, User user, String lang) {
		Calendar cal = Calendar.getInstance();

		System.out.println("START== " + cal.getTime());

		// Hold the new Book Pages in memory
		Iterable<Page> newBookPages = pagesRepository.searchByBookId(newBook.getId()); // mongo
																						// data
		System.out.println("newBookPages Info-->" + newBookPages.toString());

		Iterable<Page> oldBookPages = pagesRepository.searchByBookId(oldBook.getId());
		System.out.println("oldBookPages Info-->" + oldBookPages.toString());

		for (Page pages : oldBookPages) {
			Page newBookPagetoMigrate = getMappedPage(pages, newBookPages);
			// get all the highlights from old page

			Iterable<Highlight> oldHiglights = highlightsRepository.searchByPageAndUser(pages.getId(), user.getId());
			Note oldNotes = notesRepository.searchByPageIdAndUserId(pages.getId(), user.getId());
			Bookmark oldBookMark = bookmarksRepository.searchByPageIdAndUserId(pages.getId(), user.getId());
			System.out.println("oldHiglights Info-->" + oldHiglights.toString());
			if (oldNotes != null)
				System.out.println("oldNotes Info-->" + oldNotes.toString());
			if (oldBookMark != null)
				System.out.println("oldBookMark Info-->" + oldBookMark.toString());

			if (newBookPagetoMigrate != null) {
				// migrate note
				if (oldNotes != null) {
					this.saveNote(oldNotes, newBookPagetoMigrate.getId(), user);
				}

				// migrate bookmark
				if (oldBookMark != null) {
					this.saveBookmark(oldBookMark, newBookPagetoMigrate, user);
				}

				// highlight
				if (!(size(oldHiglights) == 0))
					this.validateHighlights(oldBook.getPath(), newBook.getPath(), pages, newBookPagetoMigrate,
							oldHiglights, lang, user);

			} else {
				// put old highlights in conflict state in page notes
				System.out.println("######Page Not Found in new Book#####->" + pages.getPageSrc());
			}
		}
		System.out.println("Migration completed");
		System.out.println("END== " + cal.getTime());
		return true;
	}

	private boolean validateHighlights(String oldBookPath, String newBookPath, Page oldPage, Page newPage,
			Iterable<Highlight> oldHiglights, String lang, User user) {
		String newPageContent = this.getFileContent(this.basePath + newBookPath + "/extracted/" + newPage.getPageSrc(),
				lang, newBookPath);
		String oldPageContent = this.getFileContent(this.basePath + oldBookPath + "/extracted/" + oldPage.getPageSrc(),
				lang, newBookPath);

		for (Highlight h : oldHiglights) {
			if (!validateSameOffset(h, newPageContent, lang, user, newPage.getId())) {
				if (!validateDiffOffSet(h, oldPageContent, newPageContent, lang, user, newPage.getId())) {
					if (!validateOneOccur(h, newPageContent, user, newPage.getId())) {
						this.saveNoteByHighlights(h, newPage.getId(), user);
						System.out.println("Highlight conflict");
					}
				}
			}
		}
		return true;
	}

	@SuppressWarnings("resource")
	private String getFileContent(String path, String lang, String bookPath) {
		String content = null;
		File file = new File(path);
		Document doc = null;
		try {
			doc = Jsoup.parse(file, "utf8");

			// remove if glossary is there
			Elements els = doc.getElementsByClass("WysiwygInLineTerm");
			for (Element el : els) {
				Element j = el.prependElement("div");
				Elements els1 = el.getElementsByClass("Title_GlossaryItem");
				for (int i = 0; i < els1.text().length(); i++) {
					j.appendText("~");
				}
			}

			// remove hidden elements
			Elements ele1 = doc.getElementsByAttributeValue("style", "display: none;");
			ele1.remove();

			// some fixes around module self check
			els = doc.getElementsByClass("Col2InnerWrapper");
			for (Element el : els) {
				Element j = el.appendElement("div");
				j.appendText("~");
			}

			org.w3c.dom.Document w3cDoc1 = DOMBuilder.jsoup2DOM(doc);
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			Source xmlSource1 = new DOMSource(w3cDoc1);
			Result outputTarget1 = new StreamResult(outStream);
			TransformerFactory.newInstance().newTransformer().transform(xmlSource1, outputTarget1);
			InputStream is1 = new ByteArrayInputStream(outStream.toByteArray());

			AutoDetectParser parser = new AutoDetectParser();
			Metadata metadata = new Metadata();

			BodyContentHandler handler = new BodyContentHandler();
			parser.parse(is1, handler, metadata);

			content = handler.toString().replaceAll("\t", "").replaceAll("\n", "");

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("File not found in path : " + path);
		}
		// }
		return content;
	}

	private boolean validateSameOffset(Highlight highlight, String newPageContent, String lang, User user,
			int newPageid) {
		String oldHighlight = highlight.getSelectedText();
		int endOffset = highlight.getEndOffset() > newPageContent.length() ? newPageContent.length()
				: highlight.getEndOffset();
		String value;
		try {
			if (newPageContent == null || newPageContent.length() <= highlight.getStartOffset()) {
				return false;
			} else if (newPageContent.length() <= endOffset) {
				value = newPageContent.substring(highlight.getStartOffset(), newPageContent.length());
			} else {
				value = newPageContent.substring(highlight.getStartOffset(), endOffset);
			}
		} catch (IndexOutOfBoundsException e) {
			System.out.println("validateSameOffset - substring : string index out of range");
			return false;
		}

		String highlightText = value.replaceAll("\n", "").replaceAll("\t", "");

		System.out.println("m===>>> " + highlightText);
		System.out.println("O===>>> " + oldHighlight);
		oldHighlight = oldHighlight.replaceAll("\\s{1}", ".").replaceAll("&lt;", "<").replaceAll("&gt;", ">")
				.replaceAll("&amp;", "&");

		if (lang.equals("en")) {
			highlightText = highlightText.replaceAll("~{2,}", "").replaceAll("\\s{1}", ".");

			if (!CharMatcher.ASCII.matchesAllOf(highlightText)) {
				highlightText = highlightText.replaceAll("\\P{Print}", ".");
			}
			oldHighlight = highlight.getSelectedText().replaceAll("\\s{1}", ".").replaceAll("&lt;", "<")
					.replaceAll("&gt;", ">").replaceAll("&amp;", "&");
			if (!CharMatcher.ASCII.matchesAllOf(oldHighlight)) {
				oldHighlight = oldHighlight.replaceAll("\\P{Print}", ".");
			}
		} else {
			oldHighlight = oldHighlight.replaceAll(" ", "#").replaceAll("\n", "");
			highlightText = highlightText.replaceAll(" ", "#").replaceAll(" ", "#");
		}
		if (highlightText.equals(oldHighlight)) {
			// store against that page in db
			System.out.println("====>>>> validateSameOffset Success !!! " + newPageContent
					.substring(highlight.getStartOffset(), highlight.getEndOffset()).replaceAll("~{2,}", ""));

		} else {
			return false;
		}

		// save to db
		saveHighlight(highlight, highlight.getStartOffset(), highlight.getEndOffset(), newPageid, user);
		return true;
	}

	private boolean saveHighlight(Highlight oldHiglight, int newStartOffset, int newEndOffset, int newPageId,
			User user) {
		System.out.println("##################################" + oldHiglight.getSelectedText());

		Iterable<Highlight> loadAddedHighlight = highlightsRepository
				.searchByStartOffsetAndEndOffsetAndPageAndUser(newStartOffset, newEndOffset, newPageId, user.getId());
		// Highlights loadAddedHighlight = hc.findHighlihtByQuery("startOffset
		// >= "+newStartOffset+" and endOffset <="+newEndOffset +" and pageId="
		// + newPageId + " and UserId=" +user.getId());
		if (size(loadAddedHighlight) == 0) {
			Highlight h = new Highlight();
			h.setId(sequenceCounterDao.getNextSequence("Highlights"));
			h.setColorOverride(oldHiglight.getColorOverride());
			h.setCreatedAt(getCurrentTimeWithFormat());
			h.setDeleted(oldHiglight.getDeleted());
			h.setEndOffset(newEndOffset);
			h.setStartOffset(newStartOffset);
			h.setGuid(java.util.UUID.randomUUID().toString());
			h.setHighlightComment(oldHiglight.getHighlightComment());
			h.setIndexed(false);
			h.setPageId(newPageId);
			h.setSelectedText(oldHiglight.getSelectedText());
			h.setUpdatedAt(getCurrentTimeWithFormat());
			h.setUserId(user.getId());
			h.setUsn(user.getLastSyncedUSN() + 1);
			highlightsRepository.save(h);
		}
		return true;
	}

	// conflict highlight convert to note and attach it to page notes
	private boolean saveNoteByHighlights(Highlight oldHighlight, int newPageId, User user) {
		Note note = notesRepository.searchByPageIdAndUserId(newPageId, user.getId());
		if (note == null) {
			note = new Note();
			note.setDeleted(false);
			note.setGuid(java.util.UUID.randomUUID().toString());
			note.setPageId(newPageId);
			note.setUserId(user.getId());

			if (oldHighlight.getHighlightComment() == null || oldHighlight.getHighlightComment() == "") {
				note.setComments("DELETED TEXT HIGHLIGHT: For text \"" + oldHighlight.getSelectedText() + "\"");
			} else {
				note.setComments("DELETED TEXT HIGHLIGHT: For text \"" + oldHighlight.getSelectedText()
						+ "\". You had the following comment: \"" + oldHighlight.getHighlightComment() + "\"");
			}
		} else {
			if (oldHighlight.getHighlightComment() == null || oldHighlight.getHighlightComment() == "") {
				note.setComments(note.getComments() + "\n\nDELETED TEXT HIGHLIGHT: For text \""
						+ oldHighlight.getSelectedText() + "\"");
			} else {
				note.setComments(
						note.getComments() + "\n\nDELETED TEXT HIGHLIGHT: For text \"" + oldHighlight.getSelectedText()
								+ "\". You had the following comment: \"" + oldHighlight.getHighlightComment() + "\"");
			}
		}
		note.setIndexed(false);
		note.setUsn(user.getLastSyncedUSN() + 1);
		this.saveNote(note, newPageId, user);
		return true;
	}

	private boolean saveNote(Note oldnote, int newPageId, User user) {
		Note note = notesRepository.searchByPageIdAndUserId(newPageId, user.getId());
		if (note == null) {
			note = new Note();
			note.setId(sequenceCounterDao.getNextSequence("Notes"));
			note.setCreatedAt(getCurrentTimeWithFormat());
			note.setDeleted(false);
			note.setGuid(java.util.UUID.randomUUID().toString());
			note.setPageId(newPageId);
			note.setUserId(user.getId());
		}
		note.setIndexed(false);
		note.setComments(oldnote.getComments());
		note.setUpdatedAt(getCurrentTimeWithFormat());
		note.setUsn(user.getLastSyncedUSN() + 1);

		notesRepository.save(note);
		return true;
	}

	private boolean saveBookmark(Bookmark oldBookmark, Page newPage, User user) {
		Bookmark bookmark = bookmarksRepository.searchByPageIdAndUserId(newPage.getId(), user.getId());
		if (bookmark == null) {
			bookmark = new Bookmark();
			bookmark.setCreatedAt(getCurrentTimeWithFormat());
			bookmark.setDeleted(false);
			bookmark.setGuid(java.util.UUID.randomUUID().toString());
			bookmark.setId(sequenceCounterDao.getNextSequence("Bookmarks"));
			bookmark.setPageId(newPage.getId());
			bookmark.setUserId(user.getId());
			bookmark.setTitle(oldBookmark.getTitle());
		}
		bookmark.setIndexed(false);
		bookmark.setUpdatedAt(getCurrentTimeWithFormat());
		bookmark.setUsn(user.getLastSyncedUSN() + 1);
		bookmarksRepository.save(bookmark);
		return true;
	}

	private boolean validateDiffOffSet(Highlight highlight, String oldBookContent, String newBookContent, String lang,
			User user, int newPageid) {
		System.out.println("Calling Diff Offset===>>> " + highlight.getSelectedText());
		int startRangeOffsetValue2 = 0, endOffRangeSetValue2 = 0;
		int startRangeOffsetValue = (int) ((highlight.getStartOffset() - 5 > 0) ? highlight.getStartOffset() - 5
				: highlight.getStartOffset());
		int endOffRangeSetValue = (int) ((highlight.getEndOffset() + 5) < oldBookContent.length()
				? highlight.getEndOffset() + 5 : highlight.getEndOffset());
		if (endOffRangeSetValue > oldBookContent.length()) {
			endOffRangeSetValue = oldBookContent.length();
			System.out.println("====$$$$$$$$ endOffRangeSetValue is equal to content length value");
		}
		String value = null;

		try {
			if (oldBookContent == null || oldBookContent.length() <= startRangeOffsetValue) {
				return false;
			} else if (oldBookContent.length() <= endOffRangeSetValue) {
				value = oldBookContent.substring(startRangeOffsetValue, oldBookContent.length());
			} else {
				value = oldBookContent.substring(startRangeOffsetValue, endOffRangeSetValue);
			}
		} catch (IndexOutOfBoundsException e) {
			System.out.println("validateDiffOffSet - substring : string index out of range");
			return false;
		}

		String originalHighlight = value;

		System.out.println("====>>>originalHighlight=" + originalHighlight);

		originalHighlight = Pattern.quote(originalHighlight);
		Pattern p = Pattern.compile(originalHighlight);
		Matcher matcher = p.matcher(newBookContent);

		matcher = p.matcher(newBookContent);
		if (matcher.find()) {
			System.out.println("====>>>Found Data");
			if (startRangeOffsetValue == 0) {
				startRangeOffsetValue2 = matcher.start();
			} else {
				startRangeOffsetValue2 = matcher.start() - 5 > 0 ? matcher.start() + 5 : matcher.start();
			}
			endOffRangeSetValue2 = matcher.end() + 5 < newBookContent.length() ? matcher.end() - 5 : matcher.end();
			System.out.println("\n\n====>>>> validateDiffOffSet Success !!! "
					+ newBookContent.substring(startRangeOffsetValue2, endOffRangeSetValue2) + " \n\n OFFSET==>>"
					+ startRangeOffsetValue2 + " // " + endOffRangeSetValue2);
			saveHighlight(highlight, startRangeOffsetValue2, endOffRangeSetValue2, newPageid, user);
			return true;
		}
		System.out.println("====>>>return false");
		return false;
	}

	private boolean validateOneOccur(Highlight highlight, String newBookContent, User user, int newPageid) {
		int count = 0;
		Pattern p = Pattern.compile(highlight.getSelectedText().replaceAll("[({})]", "."));
		Matcher matcher = p.matcher(newBookContent);
		while (matcher.find() && count < 2) {
			count++;
		}
		matcher = p.matcher(newBookContent);
		if (count == 1 && matcher.find()) {
			System.out.println("====>>>> validateOneOccur Success !!! "
					+ newBookContent.substring(matcher.start(), matcher.end()));
			saveHighlight(highlight, matcher.start(), matcher.end(), newPageid, user);
			return true;
		}
		return false;
	}

	private Page getMappedPage(final Page oldPage, Iterable<Page> newBookPages) {
		Page mappedPageinNewBook = Predicate.select(newBookPages, new IPredicate<Page>() {
			public boolean apply(Page page) {
				// find the page in new book by guid/filepath
				String oldPageSrc = oldPage.getPageSrc();
				boolean foundByPath = page.getPageSrc().equals(oldPageSrc);

				// find the page in new book by page guid
				boolean foundByGuid = false;
				if (!foundByPath) {
					String pattern = "[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}(\\}){0,1}";
					Pattern r = Pattern.compile(pattern);
					Matcher m = r.matcher(oldPageSrc);
					if (m.find()) {
						foundByGuid = page.getPageSrc().contains(m.group(0));
					}
				}
				return foundByPath || foundByGuid;
			}
		});
		return mappedPageinNewBook;
	}
}
