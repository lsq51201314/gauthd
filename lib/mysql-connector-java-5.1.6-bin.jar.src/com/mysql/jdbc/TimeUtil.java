/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.sql.Date;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import java.util.TimeZone;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class TimeUtil
/*      */ {
/*      */   static final Map ABBREVIATED_TIMEZONES;
/*   46 */   static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
/*      */   
/*      */   static final Map TIMEZONE_MAPPINGS;
/*      */   
/*      */   static {
/*   51 */     HashMap tempMap = new HashMap();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*   56 */     tempMap.put("Romance", "Europe/Paris");
/*   57 */     tempMap.put("Romance Standard Time", "Europe/Paris");
/*   58 */     tempMap.put("Warsaw", "Europe/Warsaw");
/*   59 */     tempMap.put("Central Europe", "Europe/Prague");
/*   60 */     tempMap.put("Central Europe Standard Time", "Europe/Prague");
/*   61 */     tempMap.put("Prague Bratislava", "Europe/Prague");
/*   62 */     tempMap.put("W. Central Africa Standard Time", "Africa/Luanda");
/*   63 */     tempMap.put("FLE", "Europe/Helsinki");
/*   64 */     tempMap.put("FLE Standard Time", "Europe/Helsinki");
/*   65 */     tempMap.put("GFT", "Europe/Athens");
/*   66 */     tempMap.put("GFT Standard Time", "Europe/Athens");
/*   67 */     tempMap.put("GTB", "Europe/Athens");
/*   68 */     tempMap.put("GTB Standard Time", "Europe/Athens");
/*   69 */     tempMap.put("Israel", "Asia/Jerusalem");
/*   70 */     tempMap.put("Israel Standard Time", "Asia/Jerusalem");
/*   71 */     tempMap.put("Arab", "Asia/Riyadh");
/*   72 */     tempMap.put("Arab Standard Time", "Asia/Riyadh");
/*   73 */     tempMap.put("Arabic Standard Time", "Asia/Baghdad");
/*   74 */     tempMap.put("E. Africa", "Africa/Nairobi");
/*   75 */     tempMap.put("E. Africa Standard Time", "Africa/Nairobi");
/*   76 */     tempMap.put("Saudi Arabia", "Asia/Riyadh");
/*   77 */     tempMap.put("Saudi Arabia Standard Time", "Asia/Riyadh");
/*   78 */     tempMap.put("Iran", "Asia/Tehran");
/*   79 */     tempMap.put("Iran Standard Time", "Asia/Tehran");
/*   80 */     tempMap.put("Afghanistan", "Asia/Kabul");
/*   81 */     tempMap.put("Afghanistan Standard Time", "Asia/Kabul");
/*   82 */     tempMap.put("India", "Asia/Calcutta");
/*   83 */     tempMap.put("India Standard Time", "Asia/Calcutta");
/*   84 */     tempMap.put("Myanmar Standard Time", "Asia/Rangoon");
/*   85 */     tempMap.put("Nepal Standard Time", "Asia/Katmandu");
/*   86 */     tempMap.put("Sri Lanka", "Asia/Colombo");
/*   87 */     tempMap.put("Sri Lanka Standard Time", "Asia/Colombo");
/*   88 */     tempMap.put("Beijing", "Asia/Shanghai");
/*   89 */     tempMap.put("China", "Asia/Shanghai");
/*   90 */     tempMap.put("China Standard Time", "Asia/Shanghai");
/*   91 */     tempMap.put("AUS Central", "Australia/Darwin");
/*   92 */     tempMap.put("AUS Central Standard Time", "Australia/Darwin");
/*   93 */     tempMap.put("Cen. Australia", "Australia/Adelaide");
/*   94 */     tempMap.put("Cen. Australia Standard Time", "Australia/Adelaide");
/*   95 */     tempMap.put("Vladivostok", "Asia/Vladivostok");
/*   96 */     tempMap.put("Vladivostok Standard Time", "Asia/Vladivostok");
/*   97 */     tempMap.put("West Pacific", "Pacific/Guam");
/*   98 */     tempMap.put("West Pacific Standard Time", "Pacific/Guam");
/*   99 */     tempMap.put("E. South America", "America/Sao_Paulo");
/*  100 */     tempMap.put("E. South America Standard Time", "America/Sao_Paulo");
/*  101 */     tempMap.put("Greenland Standard Time", "America/Godthab");
/*  102 */     tempMap.put("Newfoundland", "America/St_Johns");
/*  103 */     tempMap.put("Newfoundland Standard Time", "America/St_Johns");
/*  104 */     tempMap.put("Pacific SA", "America/Caracas");
/*  105 */     tempMap.put("Pacific SA Standard Time", "America/Caracas");
/*  106 */     tempMap.put("SA Western", "America/Caracas");
/*  107 */     tempMap.put("SA Western Standard Time", "America/Caracas");
/*  108 */     tempMap.put("SA Pacific", "America/Bogota");
/*  109 */     tempMap.put("SA Pacific Standard Time", "America/Bogota");
/*  110 */     tempMap.put("US Eastern", "America/Indianapolis");
/*  111 */     tempMap.put("US Eastern Standard Time", "America/Indianapolis");
/*  112 */     tempMap.put("Central America Standard Time", "America/Regina");
/*  113 */     tempMap.put("Mexico", "America/Mexico_City");
/*  114 */     tempMap.put("Mexico Standard Time", "America/Mexico_City");
/*  115 */     tempMap.put("Canada Central", "America/Regina");
/*  116 */     tempMap.put("Canada Central Standard Time", "America/Regina");
/*  117 */     tempMap.put("US Mountain", "America/Phoenix");
/*  118 */     tempMap.put("US Mountain Standard Time", "America/Phoenix");
/*  119 */     tempMap.put("GMT", "GMT");
/*  120 */     tempMap.put("Ekaterinburg", "Asia/Yekaterinburg");
/*  121 */     tempMap.put("Ekaterinburg Standard Time", "Asia/Yekaterinburg");
/*  122 */     tempMap.put("West Asia", "Asia/Karachi");
/*  123 */     tempMap.put("West Asia Standard Time", "Asia/Karachi");
/*  124 */     tempMap.put("Central Asia", "Asia/Dhaka");
/*  125 */     tempMap.put("Central Asia Standard Time", "Asia/Dhaka");
/*  126 */     tempMap.put("N. Central Asia Standard Time", "Asia/Novosibirsk");
/*  127 */     tempMap.put("Bangkok", "Asia/Bangkok");
/*  128 */     tempMap.put("Bangkok Standard Time", "Asia/Bangkok");
/*  129 */     tempMap.put("North Asia Standard Time", "Asia/Krasnoyarsk");
/*  130 */     tempMap.put("SE Asia", "Asia/Bangkok");
/*  131 */     tempMap.put("SE Asia Standard Time", "Asia/Bangkok");
/*  132 */     tempMap.put("North Asia East Standard Time", "Asia/Ulaanbaatar");
/*  133 */     tempMap.put("Singapore", "Asia/Singapore");
/*  134 */     tempMap.put("Singapore Standard Time", "Asia/Singapore");
/*  135 */     tempMap.put("Taipei", "Asia/Taipei");
/*  136 */     tempMap.put("Taipei Standard Time", "Asia/Taipei");
/*  137 */     tempMap.put("W. Australia", "Australia/Perth");
/*  138 */     tempMap.put("W. Australia Standard Time", "Australia/Perth");
/*  139 */     tempMap.put("Korea", "Asia/Seoul");
/*  140 */     tempMap.put("Korea Standard Time", "Asia/Seoul");
/*  141 */     tempMap.put("Tokyo", "Asia/Tokyo");
/*  142 */     tempMap.put("Tokyo Standard Time", "Asia/Tokyo");
/*  143 */     tempMap.put("Yakutsk", "Asia/Yakutsk");
/*  144 */     tempMap.put("Yakutsk Standard Time", "Asia/Yakutsk");
/*  145 */     tempMap.put("Central European", "Europe/Belgrade");
/*  146 */     tempMap.put("Central European Standard Time", "Europe/Belgrade");
/*  147 */     tempMap.put("W. Europe", "Europe/Berlin");
/*  148 */     tempMap.put("W. Europe Standard Time", "Europe/Berlin");
/*  149 */     tempMap.put("Tasmania", "Australia/Hobart");
/*  150 */     tempMap.put("Tasmania Standard Time", "Australia/Hobart");
/*  151 */     tempMap.put("AUS Eastern", "Australia/Sydney");
/*  152 */     tempMap.put("AUS Eastern Standard Time", "Australia/Sydney");
/*  153 */     tempMap.put("E. Australia", "Australia/Brisbane");
/*  154 */     tempMap.put("E. Australia Standard Time", "Australia/Brisbane");
/*  155 */     tempMap.put("Sydney Standard Time", "Australia/Sydney");
/*  156 */     tempMap.put("Central Pacific", "Pacific/Guadalcanal");
/*  157 */     tempMap.put("Central Pacific Standard Time", "Pacific/Guadalcanal");
/*  158 */     tempMap.put("Dateline", "Pacific/Majuro");
/*  159 */     tempMap.put("Dateline Standard Time", "Pacific/Majuro");
/*  160 */     tempMap.put("Fiji", "Pacific/Fiji");
/*  161 */     tempMap.put("Fiji Standard Time", "Pacific/Fiji");
/*  162 */     tempMap.put("Samoa", "Pacific/Apia");
/*  163 */     tempMap.put("Samoa Standard Time", "Pacific/Apia");
/*  164 */     tempMap.put("Hawaiian", "Pacific/Honolulu");
/*  165 */     tempMap.put("Hawaiian Standard Time", "Pacific/Honolulu");
/*  166 */     tempMap.put("Alaskan", "America/Anchorage");
/*  167 */     tempMap.put("Alaskan Standard Time", "America/Anchorage");
/*  168 */     tempMap.put("Pacific", "America/Los_Angeles");
/*  169 */     tempMap.put("Pacific Standard Time", "America/Los_Angeles");
/*  170 */     tempMap.put("Mexico Standard Time 2", "America/Chihuahua");
/*  171 */     tempMap.put("Mountain", "America/Denver");
/*  172 */     tempMap.put("Mountain Standard Time", "America/Denver");
/*  173 */     tempMap.put("Central", "America/Chicago");
/*  174 */     tempMap.put("Central Standard Time", "America/Chicago");
/*  175 */     tempMap.put("Eastern", "America/New_York");
/*  176 */     tempMap.put("Eastern Standard Time", "America/New_York");
/*  177 */     tempMap.put("E. Europe", "Europe/Bucharest");
/*  178 */     tempMap.put("E. Europe Standard Time", "Europe/Bucharest");
/*  179 */     tempMap.put("Egypt", "Africa/Cairo");
/*  180 */     tempMap.put("Egypt Standard Time", "Africa/Cairo");
/*  181 */     tempMap.put("South Africa", "Africa/Harare");
/*  182 */     tempMap.put("South Africa Standard Time", "Africa/Harare");
/*  183 */     tempMap.put("Atlantic", "America/Halifax");
/*  184 */     tempMap.put("Atlantic Standard Time", "America/Halifax");
/*  185 */     tempMap.put("SA Eastern", "America/Buenos_Aires");
/*  186 */     tempMap.put("SA Eastern Standard Time", "America/Buenos_Aires");
/*  187 */     tempMap.put("Mid-Atlantic", "Atlantic/South_Georgia");
/*  188 */     tempMap.put("Mid-Atlantic Standard Time", "Atlantic/South_Georgia");
/*  189 */     tempMap.put("Azores", "Atlantic/Azores");
/*  190 */     tempMap.put("Azores Standard Time", "Atlantic/Azores");
/*  191 */     tempMap.put("Cape Verde Standard Time", "Atlantic/Cape_Verde");
/*  192 */     tempMap.put("Russian", "Europe/Moscow");
/*  193 */     tempMap.put("Russian Standard Time", "Europe/Moscow");
/*  194 */     tempMap.put("New Zealand", "Pacific/Auckland");
/*  195 */     tempMap.put("New Zealand Standard Time", "Pacific/Auckland");
/*  196 */     tempMap.put("Tonga Standard Time", "Pacific/Tongatapu");
/*  197 */     tempMap.put("Arabian", "Asia/Muscat");
/*  198 */     tempMap.put("Arabian Standard Time", "Asia/Muscat");
/*  199 */     tempMap.put("Caucasus", "Asia/Tbilisi");
/*  200 */     tempMap.put("Caucasus Standard Time", "Asia/Tbilisi");
/*  201 */     tempMap.put("GMT Standard Time", "GMT");
/*  202 */     tempMap.put("Greenwich", "GMT");
/*  203 */     tempMap.put("Greenwich Standard Time", "GMT");
/*  204 */     tempMap.put("UTC", "GMT");
/*      */     
/*  206 */     TIMEZONE_MAPPINGS = Collections.unmodifiableMap(tempMap);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  211 */     tempMap = new HashMap();
/*      */     
/*  213 */     tempMap.put("ACST", new String[] { "America/Porto_Acre" });
/*  214 */     tempMap.put("ACT", new String[] { "America/Porto_Acre" });
/*  215 */     tempMap.put("ADDT", new String[] { "America/Pangnirtung" });
/*  216 */     tempMap.put("ADMT", new String[] { "Africa/Asmera", "Africa/Addis_Ababa" });
/*      */     
/*  218 */     tempMap.put("ADT", new String[] { "Atlantic/Bermuda", "Asia/Baghdad", "America/Thule", "America/Goose_Bay", "America/Halifax", "America/Glace_Bay", "America/Pangnirtung", "America/Barbados", "America/Martinique" });
/*      */ 
/*      */ 
/*      */     
/*  222 */     tempMap.put("AFT", new String[] { "Asia/Kabul" });
/*  223 */     tempMap.put("AHDT", new String[] { "America/Anchorage" });
/*  224 */     tempMap.put("AHST", new String[] { "America/Anchorage" });
/*  225 */     tempMap.put("AHWT", new String[] { "America/Anchorage" });
/*  226 */     tempMap.put("AKDT", new String[] { "America/Juneau", "America/Yakutat", "America/Anchorage", "America/Nome" });
/*      */     
/*  228 */     tempMap.put("AKST", new String[] { "Asia/Aqtobe", "America/Juneau", "America/Yakutat", "America/Anchorage", "America/Nome" });
/*      */     
/*  230 */     tempMap.put("AKT", new String[] { "Asia/Aqtobe" });
/*  231 */     tempMap.put("AKTST", new String[] { "Asia/Aqtobe" });
/*  232 */     tempMap.put("AKWT", new String[] { "America/Juneau", "America/Yakutat", "America/Anchorage", "America/Nome" });
/*      */     
/*  234 */     tempMap.put("ALMST", new String[] { "Asia/Almaty" });
/*  235 */     tempMap.put("ALMT", new String[] { "Asia/Almaty" });
/*  236 */     tempMap.put("AMST", new String[] { "Asia/Yerevan", "America/Cuiaba", "America/Porto_Velho", "America/Boa_Vista", "America/Manaus" });
/*      */     
/*  238 */     tempMap.put("AMT", new String[] { "Europe/Athens", "Europe/Amsterdam", "Asia/Yerevan", "Africa/Asmera", "America/Cuiaba", "America/Porto_Velho", "America/Boa_Vista", "America/Manaus", "America/Asuncion" });
/*      */ 
/*      */ 
/*      */     
/*  242 */     tempMap.put("ANAMT", new String[] { "Asia/Anadyr" });
/*  243 */     tempMap.put("ANAST", new String[] { "Asia/Anadyr" });
/*  244 */     tempMap.put("ANAT", new String[] { "Asia/Anadyr" });
/*  245 */     tempMap.put("ANT", new String[] { "America/Aruba", "America/Curacao" });
/*  246 */     tempMap.put("AQTST", new String[] { "Asia/Aqtobe", "Asia/Aqtau" });
/*  247 */     tempMap.put("AQTT", new String[] { "Asia/Aqtobe", "Asia/Aqtau" });
/*  248 */     tempMap.put("ARST", new String[] { "Antarctica/Palmer", "America/Buenos_Aires", "America/Rosario", "America/Cordoba", "America/Jujuy", "America/Catamarca", "America/Mendoza" });
/*      */ 
/*      */     
/*  251 */     tempMap.put("ART", new String[] { "Antarctica/Palmer", "America/Buenos_Aires", "America/Rosario", "America/Cordoba", "America/Jujuy", "America/Catamarca", "America/Mendoza" });
/*      */ 
/*      */     
/*  254 */     tempMap.put("ASHST", new String[] { "Asia/Ashkhabad" });
/*  255 */     tempMap.put("ASHT", new String[] { "Asia/Ashkhabad" });
/*  256 */     tempMap.put("AST", new String[] { "Atlantic/Bermuda", "Asia/Bahrain", "Asia/Baghdad", "Asia/Kuwait", "Asia/Qatar", "Asia/Riyadh", "Asia/Aden", "America/Thule", "America/Goose_Bay", "America/Halifax", "America/Glace_Bay", "America/Pangnirtung", "America/Anguilla", "America/Antigua", "America/Barbados", "America/Dominica", "America/Santo_Domingo", "America/Grenada", "America/Guadeloupe", "America/Martinique", "America/Montserrat", "America/Puerto_Rico", "America/St_Kitts", "America/St_Lucia", "America/Miquelon", "America/St_Vincent", "America/Tortola", "America/St_Thomas", "America/Aruba", "America/Curacao", "America/Port_of_Spain" });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  267 */     tempMap.put("AWT", new String[] { "America/Puerto_Rico" });
/*  268 */     tempMap.put("AZOST", new String[] { "Atlantic/Azores" });
/*  269 */     tempMap.put("AZOT", new String[] { "Atlantic/Azores" });
/*  270 */     tempMap.put("AZST", new String[] { "Asia/Baku" });
/*  271 */     tempMap.put("AZT", new String[] { "Asia/Baku" });
/*  272 */     tempMap.put("BAKST", new String[] { "Asia/Baku" });
/*  273 */     tempMap.put("BAKT", new String[] { "Asia/Baku" });
/*  274 */     tempMap.put("BDT", new String[] { "Asia/Dacca", "America/Nome", "America/Adak" });
/*      */     
/*  276 */     tempMap.put("BEAT", new String[] { "Africa/Nairobi", "Africa/Mogadishu", "Africa/Kampala" });
/*      */     
/*  278 */     tempMap.put("BEAUT", new String[] { "Africa/Nairobi", "Africa/Dar_es_Salaam", "Africa/Kampala" });
/*      */     
/*  280 */     tempMap.put("BMT", new String[] { "Europe/Brussels", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Bucharest", "Europe/Zurich", "Asia/Baghdad", "Asia/Bangkok", "Africa/Banjul", "America/Barbados", "America/Bogota" });
/*      */ 
/*      */ 
/*      */     
/*  284 */     tempMap.put("BNT", new String[] { "Asia/Brunei" });
/*  285 */     tempMap.put("BORT", new String[] { "Asia/Ujung_Pandang", "Asia/Kuching" });
/*      */     
/*  287 */     tempMap.put("BOST", new String[] { "America/La_Paz" });
/*  288 */     tempMap.put("BOT", new String[] { "America/La_Paz" });
/*  289 */     tempMap.put("BRST", new String[] { "America/Belem", "America/Fortaleza", "America/Araguaina", "America/Maceio", "America/Sao_Paulo" });
/*      */ 
/*      */     
/*  292 */     tempMap.put("BRT", new String[] { "America/Belem", "America/Fortaleza", "America/Araguaina", "America/Maceio", "America/Sao_Paulo" });
/*      */     
/*  294 */     tempMap.put("BST", new String[] { "Europe/London", "Europe/Belfast", "Europe/Dublin", "Europe/Gibraltar", "Pacific/Pago_Pago", "Pacific/Midway", "America/Nome", "America/Adak" });
/*      */ 
/*      */     
/*  297 */     tempMap.put("BTT", new String[] { "Asia/Thimbu" });
/*  298 */     tempMap.put("BURT", new String[] { "Asia/Dacca", "Asia/Rangoon", "Asia/Calcutta" });
/*      */     
/*  300 */     tempMap.put("BWT", new String[] { "America/Nome", "America/Adak" });
/*  301 */     tempMap.put("CANT", new String[] { "Atlantic/Canary" });
/*  302 */     tempMap.put("CAST", new String[] { "Africa/Gaborone", "Africa/Khartoum" });
/*      */     
/*  304 */     tempMap.put("CAT", new String[] { "Africa/Gaborone", "Africa/Bujumbura", "Africa/Lubumbashi", "Africa/Blantyre", "Africa/Maputo", "Africa/Windhoek", "Africa/Kigali", "Africa/Khartoum", "Africa/Lusaka", "Africa/Harare", "America/Anchorage" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  309 */     tempMap.put("CCT", new String[] { "Indian/Cocos" });
/*  310 */     tempMap.put("CDDT", new String[] { "America/Rankin_Inlet" });
/*  311 */     tempMap.put("CDT", new String[] { "Asia/Harbin", "Asia/Shanghai", "Asia/Chungking", "Asia/Urumqi", "Asia/Kashgar", "Asia/Taipei", "Asia/Macao", "America/Chicago", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Knox", "America/Indiana/Vevay", "America/Louisville", "America/Menominee", "America/Rainy_River", "America/Winnipeg", "America/Pangnirtung", "America/Iqaluit", "America/Rankin_Inlet", "America/Cambridge_Bay", "America/Cancun", "America/Mexico_City", "America/Chihuahua", "America/Belize", "America/Costa_Rica", "America/Havana", "America/El_Salvador", "America/Guatemala", "America/Tegucigalpa", "America/Managua" });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  323 */     tempMap.put("CEST", new String[] { "Europe/Tirane", "Europe/Andorra", "Europe/Vienna", "Europe/Minsk", "Europe/Brussels", "Europe/Sofia", "Europe/Prague", "Europe/Copenhagen", "Europe/Tallinn", "Europe/Berlin", "Europe/Gibraltar", "Europe/Athens", "Europe/Budapest", "Europe/Rome", "Europe/Riga", "Europe/Vaduz", "Europe/Vilnius", "Europe/Luxembourg", "Europe/Malta", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Monaco", "Europe/Amsterdam", "Europe/Oslo", "Europe/Warsaw", "Europe/Lisbon", "Europe/Kaliningrad", "Europe/Madrid", "Europe/Stockholm", "Europe/Zurich", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Europe/Simferopol", "Europe/Belgrade", "Africa/Algiers", "Africa/Tripoli", "Africa/Tunis", "Africa/Ceuta" });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  337 */     tempMap.put("CET", new String[] { "Europe/Tirane", "Europe/Andorra", "Europe/Vienna", "Europe/Minsk", "Europe/Brussels", "Europe/Sofia", "Europe/Prague", "Europe/Copenhagen", "Europe/Tallinn", "Europe/Berlin", "Europe/Gibraltar", "Europe/Athens", "Europe/Budapest", "Europe/Rome", "Europe/Riga", "Europe/Vaduz", "Europe/Vilnius", "Europe/Luxembourg", "Europe/Malta", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Monaco", "Europe/Amsterdam", "Europe/Oslo", "Europe/Warsaw", "Europe/Lisbon", "Europe/Kaliningrad", "Europe/Madrid", "Europe/Stockholm", "Europe/Zurich", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Europe/Simferopol", "Europe/Belgrade", "Africa/Algiers", "Africa/Tripoli", "Africa/Casablanca", "Africa/Tunis", "Africa/Ceuta" });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  351 */     tempMap.put("CGST", new String[] { "America/Scoresbysund" });
/*  352 */     tempMap.put("CGT", new String[] { "America/Scoresbysund" });
/*  353 */     tempMap.put("CHDT", new String[] { "America/Belize" });
/*  354 */     tempMap.put("CHUT", new String[] { "Asia/Chungking" });
/*  355 */     tempMap.put("CJT", new String[] { "Asia/Tokyo" });
/*  356 */     tempMap.put("CKHST", new String[] { "Pacific/Rarotonga" });
/*  357 */     tempMap.put("CKT", new String[] { "Pacific/Rarotonga" });
/*  358 */     tempMap.put("CLST", new String[] { "Antarctica/Palmer", "America/Santiago" });
/*      */     
/*  360 */     tempMap.put("CLT", new String[] { "Antarctica/Palmer", "America/Santiago" });
/*      */     
/*  362 */     tempMap.put("CMT", new String[] { "Europe/Copenhagen", "Europe/Chisinau", "Europe/Tiraspol", "America/St_Lucia", "America/Buenos_Aires", "America/Rosario", "America/Cordoba", "America/Jujuy", "America/Catamarca", "America/Mendoza", "America/Caracas" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  367 */     tempMap.put("COST", new String[] { "America/Bogota" });
/*  368 */     tempMap.put("COT", new String[] { "America/Bogota" });
/*  369 */     tempMap.put("CST", new String[] { "Asia/Harbin", "Asia/Shanghai", "Asia/Chungking", "Asia/Urumqi", "Asia/Kashgar", "Asia/Taipei", "Asia/Macao", "Asia/Jayapura", "Australia/Darwin", "Australia/Adelaide", "Australia/Broken_Hill", "America/Chicago", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Knox", "America/Indiana/Vevay", "America/Louisville", "America/Detroit", "America/Menominee", "America/Rainy_River", "America/Winnipeg", "America/Regina", "America/Swift_Current", "America/Pangnirtung", "America/Iqaluit", "America/Rankin_Inlet", "America/Cambridge_Bay", "America/Cancun", "America/Mexico_City", "America/Chihuahua", "America/Hermosillo", "America/Mazatlan", "America/Belize", "America/Costa_Rica", "America/Havana", "America/El_Salvador", "America/Guatemala", "America/Tegucigalpa", "America/Managua" });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  389 */     tempMap.put("CUT", new String[] { "Europe/Zaporozhye" });
/*  390 */     tempMap.put("CVST", new String[] { "Atlantic/Cape_Verde" });
/*  391 */     tempMap.put("CVT", new String[] { "Atlantic/Cape_Verde" });
/*  392 */     tempMap.put("CWT", new String[] { "America/Chicago", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Knox", "America/Indiana/Vevay", "America/Louisville", "America/Menominee" });
/*      */ 
/*      */ 
/*      */     
/*  396 */     tempMap.put("CXT", new String[] { "Indian/Christmas" });
/*  397 */     tempMap.put("DACT", new String[] { "Asia/Dacca" });
/*  398 */     tempMap.put("DAVT", new String[] { "Antarctica/Davis" });
/*  399 */     tempMap.put("DDUT", new String[] { "Antarctica/DumontDUrville" });
/*  400 */     tempMap.put("DFT", new String[] { "Europe/Oslo", "Europe/Paris" });
/*  401 */     tempMap.put("DMT", new String[] { "Europe/Belfast", "Europe/Dublin" });
/*  402 */     tempMap.put("DUSST", new String[] { "Asia/Dushanbe" });
/*  403 */     tempMap.put("DUST", new String[] { "Asia/Dushanbe" });
/*  404 */     tempMap.put("EASST", new String[] { "Pacific/Easter" });
/*  405 */     tempMap.put("EAST", new String[] { "Indian/Antananarivo", "Pacific/Easter" });
/*      */     
/*  407 */     tempMap.put("EAT", new String[] { "Indian/Comoro", "Indian/Antananarivo", "Indian/Mayotte", "Africa/Djibouti", "Africa/Asmera", "Africa/Addis_Ababa", "Africa/Nairobi", "Africa/Mogadishu", "Africa/Khartoum", "Africa/Dar_es_Salaam", "Africa/Kampala" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  412 */     tempMap.put("ECT", new String[] { "Pacific/Galapagos", "America/Guayaquil" });
/*      */     
/*  414 */     tempMap.put("EDDT", new String[] { "America/Iqaluit" });
/*  415 */     tempMap.put("EDT", new String[] { "America/New_York", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Vevay", "America/Louisville", "America/Detroit", "America/Montreal", "America/Thunder_Bay", "America/Nipigon", "America/Pangnirtung", "America/Iqaluit", "America/Cancun", "America/Nassau", "America/Santo_Domingo", "America/Port-au-Prince", "America/Jamaica", "America/Grand_Turk" });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  423 */     tempMap.put("EEMT", new String[] { "Europe/Minsk", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Kaliningrad", "Europe/Moscow" });
/*      */     
/*  425 */     tempMap.put("EEST", new String[] { "Europe/Minsk", "Europe/Sofia", "Europe/Tallinn", "Europe/Helsinki", "Europe/Athens", "Europe/Riga", "Europe/Vilnius", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Warsaw", "Europe/Bucharest", "Europe/Kaliningrad", "Europe/Moscow", "Europe/Istanbul", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Asia/Nicosia", "Asia/Amman", "Asia/Beirut", "Asia/Gaza", "Asia/Damascus", "Africa/Cairo" });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  433 */     tempMap.put("EET", new String[] { "Europe/Minsk", "Europe/Sofia", "Europe/Tallinn", "Europe/Helsinki", "Europe/Athens", "Europe/Riga", "Europe/Vilnius", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Warsaw", "Europe/Bucharest", "Europe/Kaliningrad", "Europe/Moscow", "Europe/Istanbul", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Europe/Simferopol", "Asia/Nicosia", "Asia/Amman", "Asia/Beirut", "Asia/Gaza", "Asia/Damascus", "Africa/Cairo", "Africa/Tripoli" });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  442 */     tempMap.put("EGST", new String[] { "America/Scoresbysund" });
/*  443 */     tempMap.put("EGT", new String[] { "Atlantic/Jan_Mayen", "America/Scoresbysund" });
/*      */     
/*  445 */     tempMap.put("EHDT", new String[] { "America/Santo_Domingo" });
/*  446 */     tempMap.put("EST", new String[] { "Australia/Brisbane", "Australia/Lindeman", "Australia/Hobart", "Australia/Melbourne", "Australia/Sydney", "Australia/Broken_Hill", "Australia/Lord_Howe", "America/New_York", "America/Chicago", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Knox", "America/Indiana/Vevay", "America/Louisville", "America/Detroit", "America/Menominee", "America/Montreal", "America/Thunder_Bay", "America/Nipigon", "America/Pangnirtung", "America/Iqaluit", "America/Cancun", "America/Antigua", "America/Nassau", "America/Cayman", "America/Santo_Domingo", "America/Port-au-Prince", "America/Jamaica", "America/Managua", "America/Panama", "America/Grand_Turk" });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  460 */     tempMap.put("EWT", new String[] { "America/New_York", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Vevay", "America/Louisville", "America/Detroit", "America/Jamaica" });
/*      */ 
/*      */ 
/*      */     
/*  464 */     tempMap.put("FFMT", new String[] { "America/Martinique" });
/*  465 */     tempMap.put("FJST", new String[] { "Pacific/Fiji" });
/*  466 */     tempMap.put("FJT", new String[] { "Pacific/Fiji" });
/*  467 */     tempMap.put("FKST", new String[] { "Atlantic/Stanley" });
/*  468 */     tempMap.put("FKT", new String[] { "Atlantic/Stanley" });
/*  469 */     tempMap.put("FMT", new String[] { "Atlantic/Madeira", "Africa/Freetown" });
/*      */     
/*  471 */     tempMap.put("FNST", new String[] { "America/Noronha" });
/*  472 */     tempMap.put("FNT", new String[] { "America/Noronha" });
/*  473 */     tempMap.put("FRUST", new String[] { "Asia/Bishkek" });
/*  474 */     tempMap.put("FRUT", new String[] { "Asia/Bishkek" });
/*  475 */     tempMap.put("GALT", new String[] { "Pacific/Galapagos" });
/*  476 */     tempMap.put("GAMT", new String[] { "Pacific/Gambier" });
/*  477 */     tempMap.put("GBGT", new String[] { "America/Guyana" });
/*  478 */     tempMap.put("GEST", new String[] { "Asia/Tbilisi" });
/*  479 */     tempMap.put("GET", new String[] { "Asia/Tbilisi" });
/*  480 */     tempMap.put("GFT", new String[] { "America/Cayenne" });
/*  481 */     tempMap.put("GHST", new String[] { "Africa/Accra" });
/*  482 */     tempMap.put("GILT", new String[] { "Pacific/Tarawa" });
/*  483 */     tempMap.put("GMT", new String[] { "Atlantic/St_Helena", "Atlantic/Reykjavik", "Europe/London", "Europe/Belfast", "Europe/Dublin", "Europe/Gibraltar", "Africa/Porto-Novo", "Africa/Ouagadougou", "Africa/Abidjan", "Africa/Malabo", "Africa/Banjul", "Africa/Accra", "Africa/Conakry", "Africa/Bissau", "Africa/Monrovia", "Africa/Bamako", "Africa/Timbuktu", "Africa/Nouakchott", "Africa/Niamey", "Africa/Sao_Tome", "Africa/Dakar", "Africa/Freetown", "Africa/Lome" });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  492 */     tempMap.put("GST", new String[] { "Atlantic/South_Georgia", "Asia/Bahrain", "Asia/Muscat", "Asia/Qatar", "Asia/Dubai", "Pacific/Guam" });
/*      */ 
/*      */     
/*  495 */     tempMap.put("GYT", new String[] { "America/Guyana" });
/*  496 */     tempMap.put("HADT", new String[] { "America/Adak" });
/*  497 */     tempMap.put("HART", new String[] { "Asia/Harbin" });
/*  498 */     tempMap.put("HAST", new String[] { "America/Adak" });
/*  499 */     tempMap.put("HAWT", new String[] { "America/Adak" });
/*  500 */     tempMap.put("HDT", new String[] { "Pacific/Honolulu" });
/*  501 */     tempMap.put("HKST", new String[] { "Asia/Hong_Kong" });
/*  502 */     tempMap.put("HKT", new String[] { "Asia/Hong_Kong" });
/*  503 */     tempMap.put("HMT", new String[] { "Atlantic/Azores", "Europe/Helsinki", "Asia/Dacca", "Asia/Calcutta", "America/Havana" });
/*      */     
/*  505 */     tempMap.put("HOVST", new String[] { "Asia/Hovd" });
/*  506 */     tempMap.put("HOVT", new String[] { "Asia/Hovd" });
/*  507 */     tempMap.put("HST", new String[] { "Pacific/Johnston", "Pacific/Honolulu" });
/*      */     
/*  509 */     tempMap.put("HWT", new String[] { "Pacific/Honolulu" });
/*  510 */     tempMap.put("ICT", new String[] { "Asia/Phnom_Penh", "Asia/Vientiane", "Asia/Bangkok", "Asia/Saigon" });
/*      */     
/*  512 */     tempMap.put("IDDT", new String[] { "Asia/Jerusalem", "Asia/Gaza" });
/*  513 */     tempMap.put("IDT", new String[] { "Asia/Jerusalem", "Asia/Gaza" });
/*  514 */     tempMap.put("IHST", new String[] { "Asia/Colombo" });
/*  515 */     tempMap.put("IMT", new String[] { "Europe/Sofia", "Europe/Istanbul", "Asia/Irkutsk" });
/*      */     
/*  517 */     tempMap.put("IOT", new String[] { "Indian/Chagos" });
/*  518 */     tempMap.put("IRKMT", new String[] { "Asia/Irkutsk" });
/*  519 */     tempMap.put("IRKST", new String[] { "Asia/Irkutsk" });
/*  520 */     tempMap.put("IRKT", new String[] { "Asia/Irkutsk" });
/*  521 */     tempMap.put("IRST", new String[] { "Asia/Tehran" });
/*  522 */     tempMap.put("IRT", new String[] { "Asia/Tehran" });
/*  523 */     tempMap.put("ISST", new String[] { "Atlantic/Reykjavik" });
/*  524 */     tempMap.put("IST", new String[] { "Atlantic/Reykjavik", "Europe/Belfast", "Europe/Dublin", "Asia/Dacca", "Asia/Thimbu", "Asia/Calcutta", "Asia/Jerusalem", "Asia/Katmandu", "Asia/Karachi", "Asia/Gaza", "Asia/Colombo" });
/*      */ 
/*      */ 
/*      */     
/*  528 */     tempMap.put("JAYT", new String[] { "Asia/Jayapura" });
/*  529 */     tempMap.put("JMT", new String[] { "Atlantic/St_Helena", "Asia/Jerusalem" });
/*      */     
/*  531 */     tempMap.put("JST", new String[] { "Asia/Rangoon", "Asia/Dili", "Asia/Ujung_Pandang", "Asia/Tokyo", "Asia/Kuala_Lumpur", "Asia/Kuching", "Asia/Manila", "Asia/Singapore", "Pacific/Nauru" });
/*      */ 
/*      */ 
/*      */     
/*  535 */     tempMap.put("KART", new String[] { "Asia/Karachi" });
/*  536 */     tempMap.put("KAST", new String[] { "Asia/Kashgar" });
/*  537 */     tempMap.put("KDT", new String[] { "Asia/Seoul" });
/*  538 */     tempMap.put("KGST", new String[] { "Asia/Bishkek" });
/*  539 */     tempMap.put("KGT", new String[] { "Asia/Bishkek" });
/*  540 */     tempMap.put("KMT", new String[] { "Europe/Vilnius", "Europe/Kiev", "America/Cayman", "America/Jamaica", "America/St_Vincent", "America/Grand_Turk" });
/*      */ 
/*      */     
/*  543 */     tempMap.put("KOST", new String[] { "Pacific/Kosrae" });
/*  544 */     tempMap.put("KRAMT", new String[] { "Asia/Krasnoyarsk" });
/*  545 */     tempMap.put("KRAST", new String[] { "Asia/Krasnoyarsk" });
/*  546 */     tempMap.put("KRAT", new String[] { "Asia/Krasnoyarsk" });
/*  547 */     tempMap.put("KST", new String[] { "Asia/Seoul", "Asia/Pyongyang" });
/*  548 */     tempMap.put("KUYMT", new String[] { "Europe/Samara" });
/*  549 */     tempMap.put("KUYST", new String[] { "Europe/Samara" });
/*  550 */     tempMap.put("KUYT", new String[] { "Europe/Samara" });
/*  551 */     tempMap.put("KWAT", new String[] { "Pacific/Kwajalein" });
/*  552 */     tempMap.put("LHST", new String[] { "Australia/Lord_Howe" });
/*  553 */     tempMap.put("LINT", new String[] { "Pacific/Kiritimati" });
/*  554 */     tempMap.put("LKT", new String[] { "Asia/Colombo" });
/*  555 */     tempMap.put("LPMT", new String[] { "America/La_Paz" });
/*  556 */     tempMap.put("LRT", new String[] { "Africa/Monrovia" });
/*  557 */     tempMap.put("LST", new String[] { "Europe/Riga" });
/*  558 */     tempMap.put("M", new String[] { "Europe/Moscow" });
/*  559 */     tempMap.put("MADST", new String[] { "Atlantic/Madeira" });
/*  560 */     tempMap.put("MAGMT", new String[] { "Asia/Magadan" });
/*  561 */     tempMap.put("MAGST", new String[] { "Asia/Magadan" });
/*  562 */     tempMap.put("MAGT", new String[] { "Asia/Magadan" });
/*  563 */     tempMap.put("MALT", new String[] { "Asia/Kuala_Lumpur", "Asia/Singapore" });
/*      */     
/*  565 */     tempMap.put("MART", new String[] { "Pacific/Marquesas" });
/*  566 */     tempMap.put("MAWT", new String[] { "Antarctica/Mawson" });
/*  567 */     tempMap.put("MDDT", new String[] { "America/Cambridge_Bay", "America/Yellowknife", "America/Inuvik" });
/*      */     
/*  569 */     tempMap.put("MDST", new String[] { "Europe/Moscow" });
/*  570 */     tempMap.put("MDT", new String[] { "America/Denver", "America/Phoenix", "America/Boise", "America/Regina", "America/Swift_Current", "America/Edmonton", "America/Cambridge_Bay", "America/Yellowknife", "America/Inuvik", "America/Chihuahua", "America/Hermosillo", "America/Mazatlan" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  575 */     tempMap.put("MET", new String[] { "Europe/Tirane", "Europe/Andorra", "Europe/Vienna", "Europe/Minsk", "Europe/Brussels", "Europe/Sofia", "Europe/Prague", "Europe/Copenhagen", "Europe/Tallinn", "Europe/Berlin", "Europe/Gibraltar", "Europe/Athens", "Europe/Budapest", "Europe/Rome", "Europe/Riga", "Europe/Vaduz", "Europe/Vilnius", "Europe/Luxembourg", "Europe/Malta", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Monaco", "Europe/Amsterdam", "Europe/Oslo", "Europe/Warsaw", "Europe/Lisbon", "Europe/Kaliningrad", "Europe/Madrid", "Europe/Stockholm", "Europe/Zurich", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Europe/Simferopol", "Europe/Belgrade", "Africa/Algiers", "Africa/Tripoli", "Africa/Casablanca", "Africa/Tunis", "Africa/Ceuta" });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  589 */     tempMap.put("MHT", new String[] { "Pacific/Majuro", "Pacific/Kwajalein" });
/*      */     
/*  591 */     tempMap.put("MMT", new String[] { "Indian/Maldives", "Europe/Minsk", "Europe/Moscow", "Asia/Rangoon", "Asia/Ujung_Pandang", "Asia/Colombo", "Pacific/Easter", "Africa/Monrovia", "America/Managua", "America/Montevideo" });
/*      */ 
/*      */ 
/*      */     
/*  595 */     tempMap.put("MOST", new String[] { "Asia/Macao" });
/*  596 */     tempMap.put("MOT", new String[] { "Asia/Macao" });
/*  597 */     tempMap.put("MPT", new String[] { "Pacific/Saipan" });
/*  598 */     tempMap.put("MSK", new String[] { "Europe/Minsk", "Europe/Tallinn", "Europe/Riga", "Europe/Vilnius", "Europe/Chisinau", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Europe/Simferopol" });
/*      */ 
/*      */ 
/*      */     
/*  602 */     tempMap.put("MST", new String[] { "Europe/Moscow", "America/Denver", "America/Phoenix", "America/Boise", "America/Regina", "America/Swift_Current", "America/Edmonton", "America/Dawson_Creek", "America/Cambridge_Bay", "America/Yellowknife", "America/Inuvik", "America/Mexico_City", "America/Chihuahua", "America/Hermosillo", "America/Mazatlan", "America/Tijuana" });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  609 */     tempMap.put("MUT", new String[] { "Indian/Mauritius" });
/*  610 */     tempMap.put("MVT", new String[] { "Indian/Maldives" });
/*  611 */     tempMap.put("MWT", new String[] { "America/Denver", "America/Phoenix", "America/Boise" });
/*      */     
/*  613 */     tempMap.put("MYT", new String[] { "Asia/Kuala_Lumpur", "Asia/Kuching" });
/*      */ 
/*      */     
/*  616 */     tempMap.put("NCST", new String[] { "Pacific/Noumea" });
/*  617 */     tempMap.put("NCT", new String[] { "Pacific/Noumea" });
/*  618 */     tempMap.put("NDT", new String[] { "America/Nome", "America/Adak", "America/St_Johns", "America/Goose_Bay" });
/*      */     
/*  620 */     tempMap.put("NEGT", new String[] { "America/Paramaribo" });
/*  621 */     tempMap.put("NFT", new String[] { "Europe/Paris", "Europe/Oslo", "Pacific/Norfolk" });
/*      */     
/*  623 */     tempMap.put("NMT", new String[] { "Pacific/Norfolk" });
/*  624 */     tempMap.put("NOVMT", new String[] { "Asia/Novosibirsk" });
/*  625 */     tempMap.put("NOVST", new String[] { "Asia/Novosibirsk" });
/*  626 */     tempMap.put("NOVT", new String[] { "Asia/Novosibirsk" });
/*  627 */     tempMap.put("NPT", new String[] { "Asia/Katmandu" });
/*  628 */     tempMap.put("NRT", new String[] { "Pacific/Nauru" });
/*  629 */     tempMap.put("NST", new String[] { "Europe/Amsterdam", "Pacific/Pago_Pago", "Pacific/Midway", "America/Nome", "America/Adak", "America/St_Johns", "America/Goose_Bay" });
/*      */ 
/*      */     
/*  632 */     tempMap.put("NUT", new String[] { "Pacific/Niue" });
/*  633 */     tempMap.put("NWT", new String[] { "America/Nome", "America/Adak" });
/*  634 */     tempMap.put("NZDT", new String[] { "Antarctica/McMurdo" });
/*  635 */     tempMap.put("NZHDT", new String[] { "Pacific/Auckland" });
/*  636 */     tempMap.put("NZST", new String[] { "Antarctica/McMurdo", "Pacific/Auckland" });
/*      */     
/*  638 */     tempMap.put("OMSMT", new String[] { "Asia/Omsk" });
/*  639 */     tempMap.put("OMSST", new String[] { "Asia/Omsk" });
/*  640 */     tempMap.put("OMST", new String[] { "Asia/Omsk" });
/*  641 */     tempMap.put("PDDT", new String[] { "America/Inuvik", "America/Whitehorse", "America/Dawson" });
/*      */     
/*  643 */     tempMap.put("PDT", new String[] { "America/Los_Angeles", "America/Juneau", "America/Boise", "America/Vancouver", "America/Dawson_Creek", "America/Inuvik", "America/Whitehorse", "America/Dawson", "America/Tijuana" });
/*      */ 
/*      */ 
/*      */     
/*  647 */     tempMap.put("PEST", new String[] { "America/Lima" });
/*  648 */     tempMap.put("PET", new String[] { "America/Lima" });
/*  649 */     tempMap.put("PETMT", new String[] { "Asia/Kamchatka" });
/*  650 */     tempMap.put("PETST", new String[] { "Asia/Kamchatka" });
/*  651 */     tempMap.put("PETT", new String[] { "Asia/Kamchatka" });
/*  652 */     tempMap.put("PGT", new String[] { "Pacific/Port_Moresby" });
/*  653 */     tempMap.put("PHOT", new String[] { "Pacific/Enderbury" });
/*  654 */     tempMap.put("PHST", new String[] { "Asia/Manila" });
/*  655 */     tempMap.put("PHT", new String[] { "Asia/Manila" });
/*  656 */     tempMap.put("PKT", new String[] { "Asia/Karachi" });
/*  657 */     tempMap.put("PMDT", new String[] { "America/Miquelon" });
/*  658 */     tempMap.put("PMMT", new String[] { "Pacific/Port_Moresby" });
/*  659 */     tempMap.put("PMST", new String[] { "America/Miquelon" });
/*  660 */     tempMap.put("PMT", new String[] { "Antarctica/DumontDUrville", "Europe/Prague", "Europe/Paris", "Europe/Monaco", "Africa/Algiers", "Africa/Tunis", "America/Panama", "America/Paramaribo" });
/*      */ 
/*      */ 
/*      */     
/*  664 */     tempMap.put("PNT", new String[] { "Pacific/Pitcairn" });
/*  665 */     tempMap.put("PONT", new String[] { "Pacific/Ponape" });
/*  666 */     tempMap.put("PPMT", new String[] { "America/Port-au-Prince" });
/*  667 */     tempMap.put("PST", new String[] { "Pacific/Pitcairn", "America/Los_Angeles", "America/Juneau", "America/Boise", "America/Vancouver", "America/Dawson_Creek", "America/Inuvik", "America/Whitehorse", "America/Dawson", "America/Hermosillo", "America/Mazatlan", "America/Tijuana" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  672 */     tempMap.put("PWT", new String[] { "Pacific/Palau", "America/Los_Angeles", "America/Juneau", "America/Boise", "America/Tijuana" });
/*      */ 
/*      */     
/*  675 */     tempMap.put("PYST", new String[] { "America/Asuncion" });
/*  676 */     tempMap.put("PYT", new String[] { "America/Asuncion" });
/*  677 */     tempMap.put("QMT", new String[] { "America/Guayaquil" });
/*  678 */     tempMap.put("RET", new String[] { "Indian/Reunion" });
/*  679 */     tempMap.put("RMT", new String[] { "Atlantic/Reykjavik", "Europe/Rome", "Europe/Riga", "Asia/Rangoon" });
/*      */     
/*  681 */     tempMap.put("S", new String[] { "Europe/Moscow" });
/*  682 */     tempMap.put("SAMMT", new String[] { "Europe/Samara" });
/*  683 */     tempMap.put("SAMST", new String[] { "Europe/Samara", "Asia/Samarkand" });
/*      */ 
/*      */     
/*  686 */     tempMap.put("SAMT", new String[] { "Europe/Samara", "Asia/Samarkand", "Pacific/Pago_Pago", "Pacific/Apia" });
/*      */     
/*  688 */     tempMap.put("SAST", new String[] { "Africa/Maseru", "Africa/Windhoek", "Africa/Johannesburg", "Africa/Mbabane" });
/*      */     
/*  690 */     tempMap.put("SBT", new String[] { "Pacific/Guadalcanal" });
/*  691 */     tempMap.put("SCT", new String[] { "Indian/Mahe" });
/*  692 */     tempMap.put("SDMT", new String[] { "America/Santo_Domingo" });
/*  693 */     tempMap.put("SGT", new String[] { "Asia/Singapore" });
/*  694 */     tempMap.put("SHEST", new String[] { "Asia/Aqtau" });
/*  695 */     tempMap.put("SHET", new String[] { "Asia/Aqtau" });
/*  696 */     tempMap.put("SJMT", new String[] { "America/Costa_Rica" });
/*  697 */     tempMap.put("SLST", new String[] { "Africa/Freetown" });
/*  698 */     tempMap.put("SMT", new String[] { "Atlantic/Stanley", "Europe/Stockholm", "Europe/Simferopol", "Asia/Phnom_Penh", "Asia/Vientiane", "Asia/Kuala_Lumpur", "Asia/Singapore", "Asia/Saigon", "America/Santiago" });
/*      */ 
/*      */ 
/*      */     
/*  702 */     tempMap.put("SRT", new String[] { "America/Paramaribo" });
/*  703 */     tempMap.put("SST", new String[] { "Pacific/Pago_Pago", "Pacific/Midway" });
/*      */     
/*  705 */     tempMap.put("SVEMT", new String[] { "Asia/Yekaterinburg" });
/*  706 */     tempMap.put("SVEST", new String[] { "Asia/Yekaterinburg" });
/*  707 */     tempMap.put("SVET", new String[] { "Asia/Yekaterinburg" });
/*  708 */     tempMap.put("SWAT", new String[] { "Africa/Windhoek" });
/*  709 */     tempMap.put("SYOT", new String[] { "Antarctica/Syowa" });
/*  710 */     tempMap.put("TAHT", new String[] { "Pacific/Tahiti" });
/*  711 */     tempMap.put("TASST", new String[] { "Asia/Samarkand", "Asia/Tashkent" });
/*      */ 
/*      */     
/*  714 */     tempMap.put("TAST", new String[] { "Asia/Samarkand", "Asia/Tashkent" });
/*  715 */     tempMap.put("TBIST", new String[] { "Asia/Tbilisi" });
/*  716 */     tempMap.put("TBIT", new String[] { "Asia/Tbilisi" });
/*  717 */     tempMap.put("TBMT", new String[] { "Asia/Tbilisi" });
/*  718 */     tempMap.put("TFT", new String[] { "Indian/Kerguelen" });
/*  719 */     tempMap.put("TJT", new String[] { "Asia/Dushanbe" });
/*  720 */     tempMap.put("TKT", new String[] { "Pacific/Fakaofo" });
/*  721 */     tempMap.put("TMST", new String[] { "Asia/Ashkhabad" });
/*  722 */     tempMap.put("TMT", new String[] { "Europe/Tallinn", "Asia/Tehran", "Asia/Ashkhabad" });
/*      */     
/*  724 */     tempMap.put("TOST", new String[] { "Pacific/Tongatapu" });
/*  725 */     tempMap.put("TOT", new String[] { "Pacific/Tongatapu" });
/*  726 */     tempMap.put("TPT", new String[] { "Asia/Dili" });
/*  727 */     tempMap.put("TRST", new String[] { "Europe/Istanbul" });
/*  728 */     tempMap.put("TRT", new String[] { "Europe/Istanbul" });
/*  729 */     tempMap.put("TRUT", new String[] { "Pacific/Truk" });
/*  730 */     tempMap.put("TVT", new String[] { "Pacific/Funafuti" });
/*  731 */     tempMap.put("ULAST", new String[] { "Asia/Ulaanbaatar" });
/*  732 */     tempMap.put("ULAT", new String[] { "Asia/Ulaanbaatar" });
/*  733 */     tempMap.put("URUT", new String[] { "Asia/Urumqi" });
/*  734 */     tempMap.put("UYHST", new String[] { "America/Montevideo" });
/*  735 */     tempMap.put("UYT", new String[] { "America/Montevideo" });
/*  736 */     tempMap.put("UZST", new String[] { "Asia/Samarkand", "Asia/Tashkent" });
/*  737 */     tempMap.put("UZT", new String[] { "Asia/Samarkand", "Asia/Tashkent" });
/*  738 */     tempMap.put("VET", new String[] { "America/Caracas" });
/*  739 */     tempMap.put("VLAMT", new String[] { "Asia/Vladivostok" });
/*  740 */     tempMap.put("VLAST", new String[] { "Asia/Vladivostok" });
/*  741 */     tempMap.put("VLAT", new String[] { "Asia/Vladivostok" });
/*  742 */     tempMap.put("VUST", new String[] { "Pacific/Efate" });
/*  743 */     tempMap.put("VUT", new String[] { "Pacific/Efate" });
/*  744 */     tempMap.put("WAKT", new String[] { "Pacific/Wake" });
/*  745 */     tempMap.put("WARST", new String[] { "America/Jujuy", "America/Mendoza" });
/*      */     
/*  747 */     tempMap.put("WART", new String[] { "America/Jujuy", "America/Mendoza" });
/*      */ 
/*      */     
/*  750 */     tempMap.put("WAST", new String[] { "Africa/Ndjamena", "Africa/Windhoek" });
/*      */     
/*  752 */     tempMap.put("WAT", new String[] { "Africa/Luanda", "Africa/Porto-Novo", "Africa/Douala", "Africa/Bangui", "Africa/Ndjamena", "Africa/Kinshasa", "Africa/Brazzaville", "Africa/Malabo", "Africa/Libreville", "Africa/Banjul", "Africa/Conakry", "Africa/Bissau", "Africa/Bamako", "Africa/Nouakchott", "Africa/El_Aaiun", "Africa/Windhoek", "Africa/Niamey", "Africa/Lagos", "Africa/Dakar", "Africa/Freetown" });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  759 */     tempMap.put("WEST", new String[] { "Atlantic/Faeroe", "Atlantic/Azores", "Atlantic/Madeira", "Atlantic/Canary", "Europe/Brussels", "Europe/Luxembourg", "Europe/Monaco", "Europe/Lisbon", "Europe/Madrid", "Africa/Algiers", "Africa/Casablanca", "Africa/Ceuta" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  764 */     tempMap.put("WET", new String[] { "Atlantic/Faeroe", "Atlantic/Azores", "Atlantic/Madeira", "Atlantic/Canary", "Europe/Andorra", "Europe/Brussels", "Europe/Luxembourg", "Europe/Monaco", "Europe/Lisbon", "Europe/Madrid", "Africa/Algiers", "Africa/Casablanca", "Africa/El_Aaiun", "Africa/Ceuta" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  769 */     tempMap.put("WFT", new String[] { "Pacific/Wallis" });
/*  770 */     tempMap.put("WGST", new String[] { "America/Godthab" });
/*  771 */     tempMap.put("WGT", new String[] { "America/Godthab" });
/*  772 */     tempMap.put("WMT", new String[] { "Europe/Vilnius", "Europe/Warsaw" });
/*  773 */     tempMap.put("WST", new String[] { "Antarctica/Casey", "Pacific/Apia", "Australia/Perth" });
/*      */     
/*  775 */     tempMap.put("YAKMT", new String[] { "Asia/Yakutsk" });
/*  776 */     tempMap.put("YAKST", new String[] { "Asia/Yakutsk" });
/*  777 */     tempMap.put("YAKT", new String[] { "Asia/Yakutsk" });
/*  778 */     tempMap.put("YAPT", new String[] { "Pacific/Yap" });
/*  779 */     tempMap.put("YDDT", new String[] { "America/Whitehorse", "America/Dawson" });
/*      */     
/*  781 */     tempMap.put("YDT", new String[] { "America/Yakutat", "America/Whitehorse", "America/Dawson" });
/*      */     
/*  783 */     tempMap.put("YEKMT", new String[] { "Asia/Yekaterinburg" });
/*  784 */     tempMap.put("YEKST", new String[] { "Asia/Yekaterinburg" });
/*  785 */     tempMap.put("YEKT", new String[] { "Asia/Yekaterinburg" });
/*  786 */     tempMap.put("YERST", new String[] { "Asia/Yerevan" });
/*  787 */     tempMap.put("YERT", new String[] { "Asia/Yerevan" });
/*  788 */     tempMap.put("YST", new String[] { "America/Yakutat", "America/Whitehorse", "America/Dawson" });
/*      */     
/*  790 */     tempMap.put("YWT", new String[] { "America/Yakutat" });
/*      */     
/*  792 */     ABBREVIATED_TIMEZONES = Collections.unmodifiableMap(tempMap);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Time changeTimezone(ConnectionImpl conn, Calendar sessionCalendar, Calendar targetCalendar, Time t, TimeZone fromTz, TimeZone toTz, boolean rollForward) {
/*  816 */     if (conn != null) {
/*  817 */       if (conn.getUseTimezone() && !conn.getNoTimezoneConversionForTimeType()) {
/*      */ 
/*      */         
/*  820 */         Calendar fromCal = Calendar.getInstance(fromTz);
/*  821 */         fromCal.setTime(t);
/*      */         
/*  823 */         int fromOffset = fromCal.get(15) + fromCal.get(16);
/*      */         
/*  825 */         Calendar toCal = Calendar.getInstance(toTz);
/*  826 */         toCal.setTime(t);
/*      */         
/*  828 */         int toOffset = toCal.get(15) + toCal.get(16);
/*      */         
/*  830 */         int offsetDiff = fromOffset - toOffset;
/*  831 */         long toTime = toCal.getTime().getTime();
/*      */         
/*  833 */         if (rollForward || (conn.isServerTzUTC() && !conn.isClientTzUTC())) {
/*  834 */           toTime += offsetDiff;
/*      */         } else {
/*  836 */           toTime -= offsetDiff;
/*      */         } 
/*      */         
/*  839 */         Time changedTime = new Time(toTime);
/*      */         
/*  841 */         return changedTime;
/*  842 */       }  if (conn.getUseJDBCCompliantTimezoneShift() && 
/*  843 */         targetCalendar != null) {
/*      */         
/*  845 */         Time adjustedTime = new Time(jdbcCompliantZoneShift(sessionCalendar, targetCalendar, t));
/*      */ 
/*      */ 
/*      */         
/*  849 */         return adjustedTime;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  854 */     return t;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Timestamp changeTimezone(ConnectionImpl conn, Calendar sessionCalendar, Calendar targetCalendar, Timestamp tstamp, TimeZone fromTz, TimeZone toTz, boolean rollForward) {
/*  878 */     if (conn != null) {
/*  879 */       if (conn.getUseTimezone()) {
/*      */         
/*  881 */         Calendar fromCal = Calendar.getInstance(fromTz);
/*  882 */         fromCal.setTime(tstamp);
/*      */         
/*  884 */         int fromOffset = fromCal.get(15) + fromCal.get(16);
/*      */         
/*  886 */         Calendar toCal = Calendar.getInstance(toTz);
/*  887 */         toCal.setTime(tstamp);
/*      */         
/*  889 */         int toOffset = toCal.get(15) + toCal.get(16);
/*      */         
/*  891 */         int offsetDiff = fromOffset - toOffset;
/*  892 */         long toTime = toCal.getTime().getTime();
/*      */         
/*  894 */         if (rollForward || (conn.isServerTzUTC() && !conn.isClientTzUTC())) {
/*  895 */           toTime += offsetDiff;
/*      */         } else {
/*  897 */           toTime -= offsetDiff;
/*      */         } 
/*      */         
/*  900 */         Timestamp changedTimestamp = new Timestamp(toTime);
/*      */         
/*  902 */         return changedTimestamp;
/*  903 */       }  if (conn.getUseJDBCCompliantTimezoneShift() && 
/*  904 */         targetCalendar != null) {
/*      */         
/*  906 */         Timestamp adjustedTimestamp = new Timestamp(jdbcCompliantZoneShift(sessionCalendar, targetCalendar, tstamp));
/*      */ 
/*      */ 
/*      */         
/*  910 */         adjustedTimestamp.setNanos(tstamp.getNanos());
/*      */         
/*  912 */         return adjustedTimestamp;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  917 */     return tstamp;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static long jdbcCompliantZoneShift(Calendar sessionCalendar, Calendar targetCalendar, Date dt) {
/*  923 */     if (sessionCalendar == null) {
/*  924 */       sessionCalendar = new GregorianCalendar();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  931 */     Date origCalDate = targetCalendar.getTime();
/*  932 */     Date origSessionDate = sessionCalendar.getTime();
/*      */     
/*      */     try {
/*  935 */       sessionCalendar.setTime(dt);
/*      */       
/*  937 */       targetCalendar.set(1, sessionCalendar.get(1));
/*  938 */       targetCalendar.set(2, sessionCalendar.get(2));
/*  939 */       targetCalendar.set(5, sessionCalendar.get(5));
/*      */       
/*  941 */       targetCalendar.set(11, sessionCalendar.get(11));
/*  942 */       targetCalendar.set(12, sessionCalendar.get(12));
/*  943 */       targetCalendar.set(13, sessionCalendar.get(13));
/*  944 */       targetCalendar.set(14, sessionCalendar.get(14));
/*      */       
/*  946 */       return targetCalendar.getTime().getTime();
/*      */     } finally {
/*      */       
/*  949 */       sessionCalendar.setTime(origSessionDate);
/*  950 */       targetCalendar.setTime(origCalDate);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static final Date fastDateCreate(boolean useGmtConversion, Calendar gmtCalIfNeeded, Calendar cal, int year, int month, int day) {
/*  962 */     Calendar dateCal = cal;
/*      */     
/*  964 */     if (useGmtConversion) {
/*      */       
/*  966 */       if (gmtCalIfNeeded == null) {
/*  967 */         gmtCalIfNeeded = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*      */       }
/*  969 */       gmtCalIfNeeded.clear();
/*      */       
/*  971 */       dateCal = gmtCalIfNeeded;
/*      */     } 
/*      */     
/*  974 */     dateCal.clear();
/*  975 */     dateCal.set(14, 0);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  980 */     dateCal.set(year, month - 1, day, 0, 0, 0);
/*      */     
/*  982 */     long dateAsMillis = 0L;
/*      */     
/*      */     try {
/*  985 */       dateAsMillis = dateCal.getTimeInMillis();
/*  986 */     } catch (IllegalAccessError iae) {
/*      */       
/*  988 */       dateAsMillis = dateCal.getTime().getTime();
/*      */     } 
/*      */     
/*  991 */     return new Date(dateAsMillis);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static final Date fastDateCreate(int year, int month, int day, Calendar targetCalendar) {
/*  997 */     Calendar dateCal = (targetCalendar == null) ? new GregorianCalendar() : targetCalendar;
/*      */     
/*  999 */     dateCal.clear();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1005 */     dateCal.set(year, month - 1, day, 0, 0, 0);
/* 1006 */     dateCal.set(14, 0);
/*      */     
/* 1008 */     long dateAsMillis = 0L;
/*      */     
/*      */     try {
/* 1011 */       dateAsMillis = dateCal.getTimeInMillis();
/* 1012 */     } catch (IllegalAccessError iae) {
/*      */       
/* 1014 */       dateAsMillis = dateCal.getTime().getTime();
/*      */     } 
/*      */     
/* 1017 */     return new Date(dateAsMillis);
/*      */   }
/*      */ 
/*      */   
/*      */   static final Time fastTimeCreate(Calendar cal, int hour, int minute, int second) throws SQLException {
/* 1022 */     if (hour < 0 || hour > 23) {
/* 1023 */       throw SQLError.createSQLException("Illegal hour value '" + hour + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1028 */     if (minute < 0 || minute > 59) {
/* 1029 */       throw SQLError.createSQLException("Illegal minute value '" + minute + "'" + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1034 */     if (second < 0 || second > 59) {
/* 1035 */       throw SQLError.createSQLException("Illegal minute value '" + second + "'" + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1040 */     cal.clear();
/*      */ 
/*      */     
/* 1043 */     cal.set(1970, 0, 1, hour, minute, second);
/*      */     
/* 1045 */     long timeAsMillis = 0L;
/*      */     
/*      */     try {
/* 1048 */       timeAsMillis = cal.getTimeInMillis();
/* 1049 */     } catch (IllegalAccessError iae) {
/*      */       
/* 1051 */       timeAsMillis = cal.getTime().getTime();
/*      */     } 
/*      */     
/* 1054 */     return new Time(timeAsMillis);
/*      */   }
/*      */ 
/*      */   
/*      */   static final Time fastTimeCreate(int hour, int minute, int second, Calendar targetCalendar) throws SQLException {
/* 1059 */     if (hour < 0 || hour > 23) {
/* 1060 */       throw SQLError.createSQLException("Illegal hour value '" + hour + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1065 */     if (minute < 0 || minute > 59) {
/* 1066 */       throw SQLError.createSQLException("Illegal minute value '" + minute + "'" + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1071 */     if (second < 0 || second > 59) {
/* 1072 */       throw SQLError.createSQLException("Illegal minute value '" + second + "'" + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1077 */     Calendar cal = (targetCalendar == null) ? new GregorianCalendar() : targetCalendar;
/* 1078 */     cal.clear();
/*      */ 
/*      */     
/* 1081 */     cal.set(1970, 0, 1, hour, minute, second);
/*      */     
/* 1083 */     long timeAsMillis = 0L;
/*      */     
/*      */     try {
/* 1086 */       timeAsMillis = cal.getTimeInMillis();
/* 1087 */     } catch (IllegalAccessError iae) {
/*      */       
/* 1089 */       timeAsMillis = cal.getTime().getTime();
/*      */     } 
/*      */     
/* 1092 */     return new Time(timeAsMillis);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static final Timestamp fastTimestampCreate(boolean useGmtConversion, Calendar gmtCalIfNeeded, Calendar cal, int year, int month, int day, int hour, int minute, int seconds, int secondsPart) {
/* 1100 */     cal.clear();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1105 */     cal.set(year, month - 1, day, hour, minute, seconds);
/*      */     
/* 1107 */     int offsetDiff = 0;
/*      */     
/* 1109 */     if (useGmtConversion) {
/* 1110 */       int fromOffset = cal.get(15) + cal.get(16);
/*      */ 
/*      */       
/* 1113 */       if (gmtCalIfNeeded == null) {
/* 1114 */         gmtCalIfNeeded = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*      */       }
/* 1116 */       gmtCalIfNeeded.clear();
/*      */       
/* 1118 */       gmtCalIfNeeded.setTimeInMillis(cal.getTimeInMillis());
/*      */       
/* 1120 */       int toOffset = gmtCalIfNeeded.get(15) + gmtCalIfNeeded.get(16);
/*      */       
/* 1122 */       offsetDiff = fromOffset - toOffset;
/*      */     } 
/*      */     
/* 1125 */     long tsAsMillis = 0L;
/*      */     
/*      */     try {
/* 1128 */       tsAsMillis = cal.getTimeInMillis();
/* 1129 */     } catch (IllegalAccessError iae) {
/*      */       
/* 1131 */       tsAsMillis = cal.getTime().getTime();
/*      */     } 
/*      */     
/* 1134 */     Timestamp ts = new Timestamp(tsAsMillis + offsetDiff);
/* 1135 */     ts.setNanos(secondsPart);
/*      */     
/* 1137 */     return ts;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static final Timestamp fastTimestampCreate(TimeZone tz, int year, int month, int day, int hour, int minute, int seconds, int secondsPart) {
/* 1143 */     Calendar cal = (tz == null) ? new GregorianCalendar() : new GregorianCalendar(tz);
/* 1144 */     cal.clear();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1149 */     cal.set(year, month - 1, day, hour, minute, seconds);
/*      */     
/* 1151 */     long tsAsMillis = 0L;
/*      */     
/*      */     try {
/* 1154 */       tsAsMillis = cal.getTimeInMillis();
/* 1155 */     } catch (IllegalAccessError iae) {
/*      */       
/* 1157 */       tsAsMillis = cal.getTime().getTime();
/*      */     } 
/*      */     
/* 1160 */     Timestamp ts = new Timestamp(tsAsMillis);
/* 1161 */     ts.setNanos(secondsPart);
/*      */     
/* 1163 */     return ts;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getCanoncialTimezone(String timezoneStr) throws SQLException {
/* 1179 */     if (timezoneStr == null) {
/* 1180 */       return null;
/*      */     }
/*      */     
/* 1183 */     timezoneStr = timezoneStr.trim();
/*      */ 
/*      */ 
/*      */     
/* 1187 */     if (timezoneStr.length() > 2 && (
/* 1188 */       timezoneStr.charAt(0) == '+' || timezoneStr.charAt(0) == '-') && Character.isDigit(timezoneStr.charAt(1)))
/*      */     {
/* 1190 */       return "GMT" + timezoneStr;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1195 */     int daylightIndex = StringUtils.indexOfIgnoreCase(timezoneStr, "DAYLIGHT");
/*      */ 
/*      */     
/* 1198 */     if (daylightIndex != -1) {
/* 1199 */       StringBuffer timezoneBuf = new StringBuffer();
/* 1200 */       timezoneBuf.append(timezoneStr.substring(0, daylightIndex));
/* 1201 */       timezoneBuf.append("Standard");
/* 1202 */       timezoneBuf.append(timezoneStr.substring(daylightIndex + "DAYLIGHT".length(), timezoneStr.length()));
/*      */       
/* 1204 */       timezoneStr = timezoneBuf.toString();
/*      */     } 
/*      */     
/* 1207 */     String canonicalTz = (String)TIMEZONE_MAPPINGS.get(timezoneStr);
/*      */ 
/*      */     
/* 1210 */     if (canonicalTz == null) {
/* 1211 */       String[] abbreviatedTimezone = (String[])ABBREVIATED_TIMEZONES.get(timezoneStr);
/*      */ 
/*      */       
/* 1214 */       if (abbreviatedTimezone != null)
/*      */       {
/* 1216 */         if (abbreviatedTimezone.length == 1) {
/* 1217 */           canonicalTz = abbreviatedTimezone[0];
/*      */         } else {
/* 1219 */           StringBuffer possibleTimezones = new StringBuffer(128);
/*      */           
/* 1221 */           possibleTimezones.append(abbreviatedTimezone[0]);
/*      */           
/* 1223 */           for (int i = 1; i < abbreviatedTimezone.length; i++) {
/* 1224 */             possibleTimezones.append(", ");
/* 1225 */             possibleTimezones.append(abbreviatedTimezone[i]);
/*      */           } 
/*      */           
/* 1228 */           throw SQLError.createSQLException(Messages.getString("TimeUtil.TooGenericTimezoneId", new Object[] { timezoneStr, possibleTimezones }), "01S00");
/*      */         } 
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1234 */     return canonicalTz;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static String timeFormattedString(int hours, int minutes, int seconds) {
/* 1243 */     StringBuffer buf = new StringBuffer(8);
/* 1244 */     if (hours < 10) {
/* 1245 */       buf.append("0");
/*      */     }
/*      */     
/* 1248 */     buf.append(hours);
/* 1249 */     buf.append(":");
/*      */     
/* 1251 */     if (minutes < 10) {
/* 1252 */       buf.append("0");
/*      */     }
/*      */     
/* 1255 */     buf.append(minutes);
/* 1256 */     buf.append(":");
/*      */     
/* 1258 */     if (seconds < 10) {
/* 1259 */       buf.append("0");
/*      */     }
/*      */     
/* 1262 */     buf.append(seconds);
/*      */     
/* 1264 */     return buf.toString();
/*      */   }
/*      */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\TimeUtil.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */