package kz.greetgo.sandbox.db.register_impl.migration;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class TagHandler implements ContentHandler {
  @Override
  public void setDocumentLocator(Locator locator) { }

  @Override
  public void startDocument() throws SAXException { }

  @Override
  public void endDocument() throws SAXException { }

  @Override
  public void startPrefixMapping(String prefix, String uri) throws SAXException { }

  @Override
  public void endPrefixMapping(String prefix) throws SAXException { }


  @Override
  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException { }

  @Override
  public void processingInstruction(String target, String data) throws SAXException { }

  @Override
  public void skippedEntity(String name) throws SAXException { }

  private final List<String> tags = new ArrayList<>();

  protected String path() {
    return "/" + tags.stream().collect(Collectors.joining("/"));
  }

  protected abstract void startTag(Attributes attributes) throws Exception;

  protected abstract void endTag() throws Exception;

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    text = null;
    tags.add(qName);
    try {
      startTag(attributes);
    } catch (Exception e) {
      if (e instanceof RuntimeException) throw (RuntimeException) e;
      if (e instanceof SAXException) throw (SAXException) e;
      throw new RuntimeException(e);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    try {
      endTag();
    } catch (Exception e) {
      if (e instanceof RuntimeException) throw (RuntimeException) e;
      if (e instanceof SAXException) throw (SAXException) e;
      throw new RuntimeException(e);
    }
    tags.remove(tags.size() - 1);
    text = null;
  }

  private StringBuilder text = null;

  protected String text() {
    if (text == null) return "";
    return text.toString();
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (text == null) text = new StringBuilder();
    text.append(ch, start, length);
  }
}
