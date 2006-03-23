// ** I18N

// Calendar SV language (Swedish, svenska)
// Author: Mihai Bazon, <mihai_bazon@yahoo.com>
// Translation team: <sv@li.org>
// Translator: Leonard Norrg\u00e5rd <leonard.norrgard@refactor.fi>
// Last translator: Leonard Norrg\u00e5rd <leonard.norrgard@refactor.fi>
// Encoding: iso-latin-1
// Distributed under the same terms as the calendar itself.

// For translators: please use UTF-8 if possible.  We strongly believe that
// Unicode is the answer to a real internationalized world.  Also please
// include your contact information in the header, as can be seen above.

// full day names
Calendar._DN = new Array
("s\u00f6ndag",
 "m\u00e5ndag",
 "tisdag",
 "onsdag",
 "torsdag",
 "fredag",
 "l\u00f6rdag",
 "s\u00f6ndag");

// Please note that the following array of short day names (and the same goes
// for short month names, _SMN) isn't absolutely necessary.  We give it here
// for exemplification on how one can customize the short day names, but if
// they are simply the first N letters of the full name you can simply say:
//
//   Calendar._SDN_len = N; // short day name length
//   Calendar._SMN_len = N; // short month name length
//
// If N = 3 then this is not needed either since we assume a value of 3 if not
// present, to be compatible with translation files that were written before
// this feature.
Calendar._SDN_len = 2;
Calendar._SMN_len = 3;

// First day of the week. "0" means display Sunday first, "1" means display
// Monday first, etc.
Calendar._FD = 1;

// full month names
Calendar._MN = new Array
("januari",
 "februari",
 "mars",
 "april",
 "maj",
 "juni",
 "juli",
 "augusti",
 "september",
 "oktober",
 "november",
 "december");

// tooltips
Calendar._TT = {};
Calendar._TT["INFO"] = "Om kalendern";

Calendar._TT["ABOUT"] =
"DHTML Datum/tid-v\u00e4ljare\n" +
"(c) dynarch.com 2002-2005 / Author: Mihai Bazon\n" + // don't translate this this ;-)
"F\u00f6r senaste version g\u00e5 till: http://www.dynarch.com/projects/calendar/\n" +
"Distribueras under GNU LGPL.  Se http://gnu.org/licenses/lgpl.html f\u00f6r detaljer." +
"\n\n" +
"Val av datum:\n" +
"- Anv\u00e4nd knapparna \xab, \xbb f\u00f6r att v\u00e4lja \u00e5r\n" +
"- Anv\u00e4nd knapparna " + String.fromCharCode(0x2039) + ", " + String.fromCharCode(0x203a) + " f\u00f6r att v\u00e4lja m\u00e5nad\n" +
"- H\u00e5ll musknappen nedtryckt p\u00e5 n\u00e5gon av ovanst\u00e5ende knappar f\u00f6r snabbare val.";
Calendar._TT["ABOUT_TIME"] = "\n\n" +
"Val av tid:\n" +
"- Klicka p\u00e5 en del av tiden f\u00f6r att \u00f6ka den delen\n" +
"- eller skift-klicka f\u00f6r att minska den\n" +
"- eller klicka och drag f\u00f6r snabbare val.";

Calendar._TT["PREV_YEAR"] = "F\u00f6reg\u00e5ende \u00e5r (h\u00e5ll f\u00f6r menu)";
Calendar._TT["PREV_MONTH"] = "F\u00f6reg\u00e5ende m\u00e5nad (h\u00e5ll f\u00f6r menu)";
Calendar._TT["GO_TODAY"] = "G\u00e5 till dagens datum";
Calendar._TT["NEXT_MONTH"] = "F\u00f6ljande m\u00e5nad (h\u00e5ll f\u00f6r menu)";
Calendar._TT["NEXT_YEAR"] = "F\u00f6ljande \u00e5r (h\u00e5ll f\u00f6r menu)";
Calendar._TT["SEL_DATE"] = "V\u00e4lj datum";
Calendar._TT["DRAG_TO_MOVE"] = "Drag f\u00f6r att flytta";
Calendar._TT["PART_TODAY"] = " (idag)";
Calendar._TT["MON_FIRST"] = "Visa m\u00e5ndag f\u00f6rst";
Calendar._TT["SUN_FIRST"] = "Visa s\u00f6ndag f\u00f6rst";
Calendar._TT["CLOSE"] = "St\u00e4ng";
Calendar._TT["TODAY"] = "Idag";
Calendar._TT["TIME_PART"] = "(Skift-)klicka eller drag f\u00f6r att \u00e4ndra tid";
Calendar._TT["DAY_FIRST"] = "Visa %s f\u00f6rst";
Calendar._TT["WEEKEND"] = "0,6";

// date formats
Calendar._TT["DEF_DATE_FORMAT"] = "%Y-%m-%d";
Calendar._TT["TT_DATE_FORMAT"] = "%A %d %b %Y";

Calendar._TT["WK"] = "vecka";
Calendar._TT["TIME"] = "tid:";
