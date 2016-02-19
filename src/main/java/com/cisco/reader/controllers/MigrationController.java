package com.cisco.reader.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
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
import org.hibernate.Session;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.cisco.reader.helpers.DOMBuilder;
import com.cisco.reader.helpers.HibernateReader;
import com.cisco.reader.helpers.predicates.IPredicate;
import com.cisco.reader.helpers.predicates.Predicate;
import com.cisco.reader.models.Bookmarks;
import com.cisco.reader.models.Books;
import com.cisco.reader.models.Highlights;
import com.cisco.reader.models.Notes;
import com.cisco.reader.models.Pages;
import com.cisco.reader.models.Users;
import com.google.common.base.CharMatcher;
import com.mysql.jdbc.log.Log;

public class MigrationController {
	// paths
	private String	basePath	= "/Users/rajad/projects/online/books/";

	public boolean migrateAnnotations(String oldBookPath, String newBookPath, String userId) {
		Session session = HibernateReader.getSessionFactory().openSession();
		BooksController b = new BooksController(session);
		Books oldBook = b.findBooksByQuery("path='" + oldBookPath + "'");
		Books newBook = b.findBooksByQuery("path='" + newBookPath + "'");

		String lang = oldBookPath.split("/")[1];

		UsersController u = new UsersController(session);
		Users user = u.findUsersByQuery("uid='" + userId + "'");

		this.migrate(oldBook, newBook, user, lang, session);
		return true;
	}

	private boolean migrate(Books oldBook, Books newBook, Users user, String lang, Session session) {
		Calendar cal = Calendar.getInstance();
		
		System.out.println("START== " + cal.getTime());
		// Hold the new Book Pages in memory
		List<Pages> newBookPages = newBook.getPages();

		for (final Pages pages : oldBook.getPages()) {
			Pages newBookPagetoMigrate = getMappedPage(pages, newBookPages);
			// get all the highlights from old page
			List<Highlights> oldHiglights = pages.getHighlights(user.getId(), session);
			Notes oldNotes = pages.getNotes(user.getId(), session);
			Bookmarks oldBookMark  = pages.getBookMarks(user.getId(), session);
			
			if (newBookPagetoMigrate != null) {
				//migrate note
				if(oldNotes!=null)
					this.saveNote(oldNotes, newBookPagetoMigrate.getId(), user);
				
				//migrate bookmark
				if(oldBookMark!=null)
					this.saveBookMark(oldBookMark, newBookPagetoMigrate, user);
				
				//highlight
				if(oldHiglights!=null && !oldHiglights.isEmpty())
					this.validateHighlights(oldBook.getPath(), newBook.getPath(), pages, newBookPagetoMigrate,oldHiglights, lang , user, session );
				 

			} else {
				// put old highlights in conflict state in page notes 
				System.out.println("######Page Not Found in new Book#####->" + pages.getPageSrc());
			}
		}
		System.out.println("Migration completed");
		System.out.println("END== " + cal.getTime());
		return true;
	}
	
	
	
	private boolean validateHighlights( String oldBookPath, String newBookPath,Pages oldPage, Pages newPage, List<Highlights>oldHighligts, String lang , Users user , Session session){
		String newPageContent = this.getFileContent( this.basePath + newBookPath +"/extracted/"+ newPage.getPageSrc(), lang, newBookPath);
		String oldPageContent = this.getFileContent(this.basePath + oldBookPath +"/extracted/"+ oldPage.getPageSrc(), lang, newBookPath);
		
		for(Highlights h:oldHighligts){
			if(!validateSameOffset(h, newPageContent, lang, user, newPage.getId())){
				if(!validateDiffOffSet(h, oldPageContent, newPageContent, lang, user, newPage.getId())){
					if(!validateOneOccur(h, newPageContent, user, newPage.getId())){
						this.saveNote(h, newPage.getId(), user);
						System.out.println("Highlight conflict");
					}
				}
			}
		}
		return true;
	}
	
	
	@SuppressWarnings("resource")
	private String getFileContent(String path, String lang,String bookPath) {
		String content = null;
		File file = new File(path);
		Document doc = null;
//		if(lang.equals("zh") || lang.equals("zh-CN")) {
//			try {
//				doc = Jsoup.parse(file, "utf8");
//	
//				// remove if glossary is there
//				Elements els = doc.getElementsByClass("WysiwygInLineTerm");
//				for (Element el : els) {
//					Element j = el.prependElement("div");
//					Elements els1 = el.getElementsByClass("Title_GlossaryItem");
//					for (int i = 0; i < els1.text().length(); i++) {
//						j.appendText("~");
//					}
//				}
//	
//				// remove hidden elements
//				Elements ele1 = doc.getElementsByAttributeValue("style", "display: none;");
//				ele1.remove();
//	
//				// some fixes around module self check
//				els = doc.getElementsByClass("Col2InnerWrapper");
//				for (Element el : els) {
//					Element j = el.appendElement("div");
//					j.appendText("~");
//				}
//	
//				org.w3c.dom.Document w3cDoc1 = DOMBuilder.jsoup2DOM(doc);
//				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//				Source xmlSource1 = new DOMSource(w3cDoc1);
//				Result outputTarget1 = new StreamResult(outStream);
//				TransformerFactory.newInstance().newTransformer().transform(xmlSource1, outputTarget1);
//				InputStream is1 = new ByteArrayInputStream(outStream.toByteArray());
//	
//				AutoDetectParser parser = new AutoDetectParser();
//				Metadata metadata = new Metadata();
//	
//				BodyContentHandler handler = new BodyContentHandler();
//				parser.parse(is1, handler, metadata);
//	
//				content = handler.toString().replaceAll("\t", "").replaceAll("\n", "");
//	
//			} catch (Exception ex) {
//				ex.printStackTrace();
//				System.out.println("File not found in path : " + path);
//			}
//			int exitStatus = 0;
//			Process process = null;
//			String tempFilePath = this.basePath + bookPath +"/extracted/temp.html";
//			File tempFile = new File(tempFilePath);
//			try {
//				//String phantomPath = "/usr/local/bin/phantomjs /Users/tilakk/Projects/newgit/annotation-migration-server/src/index.js "+path+" "+tempFilePath;
//				String[] cmdArray = new String[]{"/usr/local/bin/phantomjs", "/Users/tilakk/Projects/newgit/annotation-migration-server/src/index.js", path, tempFilePath};
//				process = Runtime.getRuntime().exec(cmdArray);
//				exitStatus = process.waitFor();
//			} 
//				catch (InterruptedException e2) {
//				e2.printStackTrace();
//			} 
//				catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//			if(exitStatus==0) {
//				try {
//					content = new Scanner(tempFile).useDelimiter("\\Z").next() ;
//					//System.out.println("firstPageContent===" + content);
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				}
//			}
//		} else {
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
//		}
		return content;
	}

	private boolean validateSameOffset(Highlights highlight, String newPageContent, String lang, Users user, int newPageid) {
		String oldHighlight = highlight.getSelectedText();
		int endOffset = highlight.getEndOffset() > newPageContent.length() ? newPageContent.length() : highlight.getEndOffset();
		String value;
		try {
			if (newPageContent == null || newPageContent.length() <= highlight.getStartOffset()) {
			    return false;
			} else if (newPageContent.length() <= endOffset) {
			    value = newPageContent.substring(highlight.getStartOffset(), newPageContent.length());
			} else { 
			    value = newPageContent.substring(highlight.getStartOffset(), endOffset);
			}
		}
		catch (IndexOutOfBoundsException e) {
			System.out.println("validateSameOffset - substring : string index out of range");
		    return false;
		}
		
		
		String highlightText = value.replaceAll("\n", "").replaceAll("\t", "");
		
		System.out.println("m===>>> " + highlightText);
		System.out.println("O===>>> " + oldHighlight);
		oldHighlight = oldHighlight.replaceAll("\\s{1}", ".").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&");

		if (lang.equals("en")) {
			highlightText = highlightText.replaceAll("~{2,}", "").replaceAll("\\s{1}", ".");

			if (!CharMatcher.ASCII.matchesAllOf(highlightText)) {
				highlightText = highlightText.replaceAll("\\P{Print}", ".");
			}
			oldHighlight = highlight.getSelectedText().replaceAll("\\s{1}", ".").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&");
			if (!CharMatcher.ASCII.matchesAllOf(oldHighlight)) {
				oldHighlight = oldHighlight.replaceAll("\\P{Print}", ".");
			}
		}else {
			oldHighlight = oldHighlight.replaceAll(" ", "#").replaceAll("\n", "");
            highlightText = highlightText.replaceAll(" ", "#").replaceAll(" ", "#");
		}
		if (highlightText.equals(oldHighlight)) {
			// store against that page in db
			System.out.println("====>>>> validateSameOffset Success !!! " + newPageContent.substring(highlight.getStartOffset(), highlight.getEndOffset()).replaceAll("~{2,}", ""));

		} else {
			return false;
		}
		
		//save to db 
		saveHighlight(highlight, highlight.getStartOffset(), highlight.getEndOffset(), newPageid, user);
		return true;
	}
	
	
	private boolean saveHighlight(Highlights oldHiglight, int newStartOffset, int newEndOffset, int newPageId, Users user) {
		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = sdf.format(dt);
		System.out.println("##################################"+oldHiglight.getSelectedText());
		Session session  = HibernateReader.getSessionFactory().openSession();
		
		HighlightsController hc = new HighlightsController(session);
		Highlights loadAddedHighlight = hc.findHighlihtByQuery("startOffset >= "+newStartOffset+" and endOffset <="+newEndOffset +" and pageId=" + newPageId + " and UserId=" +user.getId());
		if(loadAddedHighlight==null){
			Highlights h = new Highlights();
			h.setColorOverride(oldHiglight.getColorOverride());
			h.setCreatedAt(currentTime);
			h.setDeleted(oldHiglight.getDeleted());
			h.setEndOffset( newEndOffset);
			h.setStartOffset(newStartOffset);
			h.setGuid(java.util.UUID.randomUUID().toString());
			h.setHighlightComment(oldHiglight.getHighlightComment());
			h.setIndexed(new Byte("0"));
			h.setPageId(newPageId);
			h.setSelectedText(oldHiglight.getSelectedText());
			h.setUpdatedAt(currentTime);
			h.setUserId(user.getId());
			h.setUsn(user.getLastSyncedUSN()+1);
		  h.setId(0);
	
		  session.beginTransaction();
			session.saveOrUpdate(h);
			session.getTransaction().commit();
		}
		session.close();
		return true;
	}
	
	//conflict highlight convert to note and attach it to page notes
	private boolean saveNote(Highlights oldHighlight, int newPageId, Users user){
		Session session  = HibernateReader.getSessionFactory().openSession();
		NotesController notesController = new NotesController(session);
		Notes note = notesController.findNotesByQuery("PageId=" + newPageId + " and UserId=" + user.getId());
		session.close();
		if(note==null){
			note = new Notes();
			note.setDeleted(new Byte("0"));
			note.setGuid(java.util.UUID.randomUUID().toString());
			note.setId(0);
			note.setPageId(newPageId);
			note.setUserId(user.getId());

			if(oldHighlight.getHighlightComment() == null || oldHighlight.getHighlightComment() == ""){
				note.setComments("DELETED TEXT HIGHLIGHT: For text \"" + oldHighlight.getSelectedText()+"\"");
			} else {
				note.setComments("DELETED TEXT HIGHLIGHT: For text \"" + oldHighlight.getSelectedText() + "\". You had the following comment: \"" + oldHighlight.getHighlightComment() + "\"");
			}
		} else {
			if(oldHighlight.getHighlightComment() == null || oldHighlight.getHighlightComment() == ""){
				note.setComments(note.getComments() + "\n\nDELETED TEXT HIGHLIGHT: For text \"" + oldHighlight.getSelectedText()+"\"");
			} else {
				note.setComments(note.getComments() + "\n\nDELETED TEXT HIGHLIGHT: For text \"" + oldHighlight.getSelectedText() + "\". You had the following comment: \"" + oldHighlight.getHighlightComment()+ "\"" );
			}
		}
		note.setIndexed(new Byte("0"));   
		note.setUsn(user.getLastSyncedUSN()+1);
		this.saveNote(note, newPageId, user);
		return true;
	}
	
	private boolean saveNote(Notes oldnote, int newPageId, Users user) {
		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = sdf.format(dt);
		Session session  = HibernateReader.getSessionFactory().openSession();
		NotesController notesController = new NotesController(session);
		Notes note = notesController.findNotesByQuery("PageId=" + newPageId + " and UserId=" + user.getId());
		if(note == null){
			note = new Notes();
			note.setCreatedAt(currentTime);
			note.setDeleted(new Byte("0"));
			note.setGuid(java.util.UUID.randomUUID().toString());
			note.setId(0);
			note.setPageId(newPageId); 
			note.setUserId(user.getId());
		} 
		note.setIndexed(new Byte("0"));
		note.setComments(oldnote.getComments());   
		note.setUpdatedAt(currentTime);
		note.setUsn(user.getLastSyncedUSN()+1);
	 
	  session.beginTransaction();
		session.saveOrUpdate(note);
		session.getTransaction().commit();
		session.close();
		return true; 
	}
	
	
	private boolean saveBookMark(Bookmarks oldBookmark, Pages newPage, Users user){
		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = sdf.format(dt);
		
		Session session  = HibernateReader.getSessionFactory().openSession();
		BookmarksController bookmarksController = new BookmarksController(session);
		Bookmarks bookmark = bookmarksController.findBookmarksByQuery("PageId=" + newPage.getId() + " and UserId=" + user.getId());
		if(bookmark == null){
			bookmark = new Bookmarks();
			bookmark.setCreatedAt(currentTime);
			bookmark.setDeleted(new Byte("0"));
			bookmark.setGuid(java.util.UUID.randomUUID().toString());
			bookmark.setId(0);
			bookmark.setPageId(newPage.getId()); 
			bookmark.setUserId(user.getId());
			bookmark.setTitle(oldBookmark.getTitle());
		} 
		bookmark.setIndexed(new Byte("0"));
		bookmark.setUpdatedAt(currentTime);
		bookmark.setUsn(user.getLastSyncedUSN()+1);
	 
	  session.beginTransaction();
		session.saveOrUpdate(bookmark);
		session.getTransaction().commit();
		session.close();
		return true; 
	}

	private boolean validateDiffOffSet(Highlights  highlight, String oldBookContent, String newBookContent, String lang, Users user, int newPageid) {
		System.out.println("Calling Diff Offset===>>> " + highlight.getSelectedText());
		int startRangeOffsetValue2=0, endOffRangeSetValue2=0;
		int startRangeOffsetValue = (highlight.getStartOffset() - 5 > 0) ? highlight.getStartOffset() - 5 : highlight.getStartOffset();
		int endOffRangeSetValue = (highlight.getEndOffset() + 5) < oldBookContent.length() ? highlight.getEndOffset() + 5 : highlight.getEndOffset();
		if(endOffRangeSetValue > oldBookContent.length()) {
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
		
		
		
//		String originalHighlight = oldBookContent.substring(startRangeOffsetValue, endOffRangeSetValue);
		String originalHighlight = value;

		System.out.println("====>>>originalHighlight="+originalHighlight);
		
		originalHighlight = Pattern.quote(originalHighlight);
		Pattern p = Pattern.compile(originalHighlight);
		Matcher matcher = p.matcher(newBookContent);
		
//		while (matcher.find() && count < 2) {
//			count++;
//		}
		matcher = p.matcher(newBookContent);
		if (matcher.find()) {
			System.out.println("====>>>Found Data");
			if (startRangeOffsetValue == 0){
				startRangeOffsetValue2 = matcher.start();
			}
			else {
				startRangeOffsetValue2 = matcher.start() - 5 > 0 ? matcher.start() + 5 : matcher.start();
			}
			endOffRangeSetValue2 = matcher.end() + 5 < newBookContent.length() ? matcher.end() - 5 : matcher.end();
			System.out.println("\n\n====>>>> validateDiffOffSet Success !!! " + newBookContent.substring(startRangeOffsetValue2, endOffRangeSetValue2) + " \n\n OFFSET==>>" + startRangeOffsetValue2 + " // " + endOffRangeSetValue2);
			saveHighlight(highlight, startRangeOffsetValue2, endOffRangeSetValue2, newPageid, user);
			return true;
		}
		System.out.println("====>>>return false");
		return false;
	}
	
	private  boolean validateOneOccur(Highlights highlight, String newBookContent, Users user, int newPageid) {
		int count = 0;
		Pattern p = Pattern.compile(highlight.getSelectedText().replaceAll("[({})]", "."));
		Matcher matcher = p.matcher(newBookContent);
		while(matcher.find() && count < 2){
		count++;
		}
		matcher = p.matcher(newBookContent);
		if(count == 1 && matcher.find()) {
		System.out.println("====>>>> validateOneOccur Success !!! " + newBookContent.substring(matcher.start(), matcher.end() ));
		saveHighlight(highlight, matcher.start(), matcher.end(), newPageid, user);
		return true;
		} 
		return false;
	}

	private Pages getMappedPage(final Pages oldPage, List<Pages> newBookPages) {
		Pages mappedPageinNewBook = Predicate.select(newBookPages, new IPredicate<Pages>() {
			public boolean apply(Pages page) {
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
