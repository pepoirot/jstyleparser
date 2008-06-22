/* Generated By:JJTree&JavaCC: Do not edit this line. CssParserConstants.java */
package cz.vutbr.web.csskit.parser;

public interface CssParserConstants {

  int EOF = 0;
  int SLASH_STAR_COMMENT = 1;
  int h = 2;
  int nonascii = 3;
  int unicode = 4;
  int escape = 5;
  int nmstart = 6;
  int nmchar = 7;
  int string1 = 8;
  int string2 = 9;
  int invalid1 = 10;
  int invalid2 = 11;
  int comment = 12;
  int ident = 13;
  int name = 14;
  int num = 15;
  int string = 16;
  int invalid = 17;
  int url = 18;
  int SPACE = 19;
  int WHITECHAR = 20;
  int LBRACE_CHAR = 21;
  int RBRACE_CHAR = 22;
  int SEMICOLON_CHAR = 23;
  int NL = 24;
  int BLANK = 25;
  int CDO = 26;
  int CDC = 27;
  int EQUAL = 28;
  int INCLUDES = 29;
  int DASHMATCH = 30;
  int LBRACE = 31;
  int RBRACE = 32;
  int SEMICOLON = 33;
  int PLUS = 34;
  int MINUS = 35;
  int GREATER = 36;
  int COMMA = 37;
  int STRING = 38;
  int INVALID = 39;
  int IDENT = 40;
  int HASH = 41;
  int IMPORT_SYM = 42;
  int PAGE_SYM = 43;
  int MEDIA_SYM = 44;
  int CHARSET_SYM = 45;
  int IMPORTANT_SYM = 46;
  int EMS = 47;
  int EXS = 48;
  int LENGTHPX = 49;
  int LENGTHCM = 50;
  int LENGTHMM = 51;
  int LENGTHPT = 52;
  int LENGTHPC = 53;
  int ANGLEDEG = 54;
  int ANGLERAD = 55;
  int ANGLEGRAD = 56;
  int TIMEMS = 57;
  int TIMES = 58;
  int FREQHZ = 59;
  int FREQKHZ = 60;
  int DIMENSION = 61;
  int PERCENTAGE = 62;
  int NUMBER = 63;
  int URI = 64;
  int FUNCTION = 65;

  int DEFAULT = 0;

  String[] tokenImage = {
    "<EOF>",
    "<SLASH_STAR_COMMENT>",
    "<h>",
    "<nonascii>",
    "<unicode>",
    "<escape>",
    "<nmstart>",
    "<nmchar>",
    "<string1>",
    "<string2>",
    "<invalid1>",
    "<invalid2>",
    "<comment>",
    "<ident>",
    "<name>",
    "<num>",
    "<string>",
    "<invalid>",
    "<url>",
    "<SPACE>",
    "<WHITECHAR>",
    "\"{\"",
    "\"}\"",
    "\";\"",
    "<NL>",
    "<BLANK>",
    "\"<!--\"",
    "\"-->\"",
    "\"=\"",
    "\"~=\"",
    "\"|=\"",
    "<LBRACE>",
    "<RBRACE>",
    "<SEMICOLON>",
    "<PLUS>",
    "<MINUS>",
    "<GREATER>",
    "<COMMA>",
    "<STRING>",
    "<INVALID>",
    "<IDENT>",
    "<HASH>",
    "\"@import\"",
    "\"@page\"",
    "\"@media\"",
    "\"@charset \"",
    "<IMPORTANT_SYM>",
    "<EMS>",
    "<EXS>",
    "<LENGTHPX>",
    "<LENGTHCM>",
    "<LENGTHMM>",
    "<LENGTHPT>",
    "<LENGTHPC>",
    "<ANGLEDEG>",
    "<ANGLERAD>",
    "<ANGLEGRAD>",
    "<TIMEMS>",
    "<TIMES>",
    "<FREQHZ>",
    "<FREQKHZ>",
    "<DIMENSION>",
    "<PERCENTAGE>",
    "<NUMBER>",
    "<URI>",
    "<FUNCTION>",
    "\":\"",
    "\".\"",
    "\"*\"",
    "\"[\"",
    "\"]\"",
    "\")\"",
    "\"/\"",
  };

}
