package teropa.globetrotter.client;

import teropa.globetrotter.client.common.LonLat;

public class DemoCities {

	public static class City {
		private final String country;
		private final String city;
		private LonLat lonLat;
		
		public City(String country, String city, double lat, double lon) {
			this.country = country;
			this.city = city;
			this.lonLat = new LonLat(lon, lat);
		}

		public LonLat getLonLat() {
			return lonLat;
		}

		public String getName() {
			return city + ", " + country;
		}
	}
	
	public static City[] CITIES = new City[] {
		// Europe
		new City("Albania",	"Tirana",	41.3317, +19.8172),
		new City("Andorra",	"Andorra la Vella", 42.5075, 1.5218),
		new City("Austria",	"Vienna", 48.2092, 16.3728),
		new City("Belarus", "Minsk", 53.9678, 27.5766),
		new City("Belgium",	"Brussels", 50.8371, +4.3676),
		new City("Bosnia and Herzegovina", "Sarajevo", 43.8608, 18.4214),
		new City("Bulgaria", "Sofia", 42.7105, 23.3238),
		new City("Croatia", "Zagreb",  45.8150, 15.9785),
		new City("Czech Republic", "Prague", 50.0878, 14.4205),
		new City("Denmark", "Copenhagen", 55.6763, 12.5681),
		new City("Estonia",	"Tallinn",	59.4389, 24.7545),
		new City("Finland",	"Helsinki",	60.1699, 24.9384),
		new City("France", "Paris", 48.8567, 2.3510),
		new City("Germany",	"Berlin", 52.5235, 13.4115),
		new City("Greece", "Athens", 37.9792, 23.7166),
		new City("Hungary", "Budapest", 47.4984, 19.0408),
		new City("Iceland",	"Reykjavik", 64.1353, -21.8952),
		new City("Ireland", "Dublin", 53.3441, -6.2675),
		new City("Italy", "Rome", 41.8955, +12.4823),
		new City("Latvia", "Riga", 56.9465, 24.1049),
		new City("Liechtenstein", "Vaduz", 47.1411, 9.5215),
		new City("Lithuania", "Vilnius", 54.6896, 25.2799),
		new City("Luxembourg", "Luxembourg", 49.6100, 6.1296),
		new City("Macedonia", "Skopje", 42.0024, 21.4361),
		new City("Malta", "Valletta", 35.9042, 14.5189),
		new City("Moldova", "Chisinau", 47.0167, 28.8497),
		new City("Monaco", "Monaco", 43.7325, 7.4189),
		new City("Montenegro", "Podgorica", 42.4602, 19.2595),
		new City("Netherlands",	"Amsterdam", 52.3738, 4.8910),
		new City("Norway", "Oslo", 59.9138, 10.7387),
		new City("Poland", "Warsaw", 52.2297, 21.0122),
		new City("Portugal", "Lisbon", 38.7072, -9.1355),
		new City("Romania", "Bucharest", 44.4479, 26.0979),
		new City("Russia", "Moscow", 55.7558, 37.6176),
		new City("San Marino", "San Marino", 43.9424, 12.4578),
		new City("Serbia", "Belgrade", 44.8048, 20.4781),
		new City("Slovakia", "Bratislava", 48.2116, 17.1547),
		new City("Slovenia", "Ljubljana", 46.0514, 14.5060),
		new City("Spain", "Madrid", 40.4167, -3.7033),
		new City("Sweden", "Stockholm",  59.3328, 18.0645),
		new City("Switzerland", "Bern", 46.9480, 7.4481),
		new City("Ukraine", "Kyiv / Kiev", 50.4422, 30.5367),
		new City("United Kingdom", "London", 51.5002, -0.1262),
		new City("Faroe Islands (DK)", "Torshavn (on Streymoy)", 62.0177, -6.7719),
		new City("Gibraltar (UK)", "Gibraltar", 36.1377, -5.3453),
		new City("Guernsey (UK)", "Saint Peter Port", 49.4660, -2.5522),
		new City("Isle of Man (UK)", "Douglas", 54.1670, -4.4821),
		new City("Jersey (UK)", "Saint Helier", 49.1919, -2.1071),
		new City("Kosovo", "Prishtine", 42.6740, +21.1788),
		new City("Svalbard (NO)", "Longyearbyen", 78.2186, 15.6488),
		
		// Asia
		new City("Afghanistan", "Kabul", 34.5155, 69.1952),
		new City("Armenia", "Yerevan", 40.1596, 44.5090),
		new City("Azerbaijan", "Baku", 40.3834, 49.8932),
		new City("Bahrain", "Manama", 26.1921, 50.5354),
		new City("Bangladesh", "Dhaka", 23.7106, 90.3978),
		new City("Bhutan", "Thimphu", 27.4405, 89.6730),
		new City("Brunei", "Bandar Seri Begawan", 4.9431, 114.9425),
		new City("Cambodia", "Phnom Penh", 11.5434, 104.8984),
		new City("China", "Beijing", 39.9056, 116.3958),
		new City("Cyprus", "Nicosia", 35.1676, 33.3736),
		new City("Georgia", "T'bilisi", 41.7010, 44.7930),
		new City("India", "New Delhi", 28.6353, 77.2250),
		new City("Indonesia", "Jakarta", -6.1862, +106.8063),
		new City("Iran", "Teheran", 35.7061, 51.4358),
		new City("Iraq", "Baghdad", 33.3157, 44.3922),
		new City("Israel", "Jerusalem", 31.7857, 35.2007),
		new City("Japan", "Tokyo", 35.6785, +139.6823),
		new City("Jordan", "Amman", 31.9394, 35.9349),
		new City("Kazakhstan", "Astana", 51.1796, +71.4475),
		new City("Kuwait", "Kuwait", 29.3721, 47.9824),
		new City("Kyrgyzstan", "Bishkek", 42.8679, 74.5984),
		new City("Laos", "Vientiane", 17.9689, 102.6137),
		new City("Lebanon", "Beirut", 33.8872, 35.5134),
		new City("Malaysia", "Kuala Lumpur", 3.1502, 101.7077),
		new City("Maldives", "Male", 4.1742, 73.5109),
		new City("Mongolia", "Ulan Bator", 47.9138, 106.9220),
		new City("Myanmar", "Pyinmana", 19.7378, 96.2083),
		new City("Nepal", "Kathmandu", 27.7058, 85.3157),
		new City("North Korea", "P'yongyang", 39.0187, 125.7468),
		new City("Oman", "Muscat", 23.6086, 58.5922),
		new City("Pakistan", "Islamabad", 33.6751, 73.0946),
		new City("Philippines", "Manila", 14.5790, 120.9726),
		new City("Qatar", "Doha", 25.2948, 51.5082),
		new City("Saudi Arabia", "Riyadh", 24.6748, 46.6977),
		new City("Singapore", "Singapore", 1.2894, 103.8500),
		new City("South Korea", "Seoul", 37.5139, 126.9828),
		new City("Sri Lanka", "Colombo", 6.9155, 79.8572),
		new City("Syria", "Damascus", 33.5158, 36.2939),
		new City("Tajikistan", "Dushanbe", 38.5737, 68.7738),
		new City("Thailand", "Bangkok", 13.7573, 100.5020),
		new City("East Timor", "Dili", -8.5662, 125.5880),
		new City("Turkey", "Ankara", 39.9439, 32.8560),
		new City("Turkmenistan", "Ashgabat", 37.9509, 58.3794),
		new City("United Arab Emirates", "Abu Dhabi", 24.4764, 54.3705),
		new City("Uzbekistan", "Tashkent", 41.3193, 69.2481),
		new City("Vietnam", "Hanoi", 21.0341, 105.8372),
		new City("Yemen", "Sanaa", 15.3556, 44.2081),
		new City("Taiwan (CN)", "Taipei", 25.0338, 121.5645)
//
//		American States (35+16)	American Capitals	Coordinates (φ / λ)	Alt. / m
//		Antigua and Barbuda	Saint John's (on Antigua)	+17.1175 / -61.8456	0
//		Argentina	Buenos Aires	-34.6118 / -58.4173	10
//		Bahamas	Nassau (on New Providence)	+25.0661 / -77.3390	2
//		Barbados	Bridgetown	+13.0935 / -59.6105	6
//		Belize	Belmopan	+17.2534 / -88.7713	59
//		Bolivia	Sucre	-19.0421 / -65.2559	2783
//		Brazil	Brasilia	-15.7801 / -47.9292	1079
//		Canada	Ottawa	+45.4235 / -75.6979	74
//		Chile	Santiago	-33.4691 / -70.6420	521
//		Colombia	Bogota	+4.6473 / -74.0962	2619
//		Costa Rica	San Jose	+9.9402 / -84.1002	1146
//		Cuba	La Habana / Havana	+23.1333 / -82.3667	4
//		Dominica	Roseau	+15.2976 / -61.3900	0
//		Dominican Republic	Santo Domingo	+18.4790 / -69.8908	0
//		Ecuador	Quito	-0.2295 / -78.5243	2763
//		El Salvador	San Salvador	+13.7034 / -89.2073	658
//		Grenada	Saint George's	+12.0540 / -61.7486	25
//		Guatemala	Guatemala	+14.6248 / -90.5328	1529
//		Guyana	Georgetown	+6.8046 / -58.1548	0
//		Haiti	Port-au-Prince	+18.5392 / -72.3288	98
//		Honduras	Tegucigalpa	+14.0821 / -87.2063	980
//		Jamaica	Kingston	+17.9927 / -76.7920	53
//		Mexico	Ciudad de Mexico	+19.4271 / -99.1276	2216
//		Nicaragua	Managua	+12.1475 / -86.2734	75
//		Panama	Panama	+8.9943 / -79.5188	0
//		Paraguay	Asuncion	-25.3005 / -57.6362	54
//		Peru	Lima	-12.0931 / -77.0465	107
//		St. Kitts and Nevis	Basseterre (on St. Kitts)	+17.2968 / -62.7138	0
//		St. Lucia	Castries	+13.9972 / -60.0018	204
//		St. Vincent and the Grenadines	Kingstown (on St. Vincent)	+13.2035 / -61.2653	0
//		Suriname	Paramaribo	+5.8232 / -55.1679	1
//		Trinidad and Tobago	Port of Spain (on Trinidad)	+10.6596 / -61.4789	0
//		United States	Washington	+38.8921 / -77.0241	2
//		Uruguay	Montevideo	-34.8941 / -56.0675	43
//		Venezuela	Caracas	+10.4961 / -66.8983	909
//		Anguilla (UK)	The Valley	+18.2249 / -63.0669	0
//		Aruba (NL)	Oranjestad	+12.5246 / -70.0265	13
//		Bermuda (UK)	Hamilton (on Main Island)	+32.2930 / -64.7820	0
//		British Virgin Islands (UK)	Road Town (on Tortola)	+18.4328 / -64.6235	0
//		Cayman Islands (UK)	George Town (on Grand Cayman)	+19.3022 / -81.3857	3
//		Falkland Islands (UK)	Stanley (on East Falkland)	-51.7010 / -57.8492	0
//		French Guiana (FR)	Cayenne	+4.9346 / -52.3303	32
//		Greenland (DK)	Nuuk	+64.1836 / -51.7214	0
//		Guadeloupe (FR)	Basse-Terre	+15.9985 / -61.7220	0
//		Martinique (FR)	Fort-de-France	+14.5997 / -61.0760	0
//		Montserrat (UK)	Plymouth	+16.6802 / -62.2014	114
//		Netherlands Antilles (NL)	Willemstad (on Curacao)	+12.1034 / -68.9335	0
//		Puerto Rico (US)	San Juan	+18.4500 / -66.0667	3
//		St. Pierre and Miquelon (FR)	Saint-Pierre (on St. Pierre)	+46.7878 / -56.1968	0
//		Turks- and Caicos Islands (UK)	Cockburn Town (on Grand Turk)	+21.4608 / -71.1363	0
//		United States Virgin Islands (US)	Charlotte Amalie (on St. Thomas)	+18.3405 / -64.9326	0
//
//		African States (53+4)	African Capitals	Coordinates (φ / λ)	Alt. / m
//		Algeria	Alger /Algiers	+36.7755 / +3.0597	0
//		Angola	Luanda	-8.8159 / +13.2306	6
//		Benin	Porto-Novo	+6.4779 / +2.6323	38
//		Botswana	Gaborone	-24.6570 / +25.9089	1014
//		Burkina Faso	Ouagadougou	+12.3569 / -1.5352	305
//		Burundi	Bujumbura	-3.3818 / +29.3622	794
//		Cameroon	Yaounde	+3.8612 / +11.5217	726
//		Cape Verde	Praia (on Sao Tiago)	+14.9195 / -23.5153	0
//		Central African Republic	Bangui	+4.3621 / +18.5873	369
//		Chad	N'djamena	+12.1121 / +15.0355	298
//		Comoros	Moroni (on Njazidja)	-11.7004 / +43.2412	110
//		Congo	Brazzaville	-4.2767 / +15.2662	155
//		Congo (Democratic Republic)	Kinshasa	-4.3369 / +15.3271	240
//		Cote d'Ivoire	Yamoussoukro	+6.8067 / -5.2728	217
//		Djibouti	Djibouti	+11.5806 / +43.1425	0
//		Egypt	Al Qahirah / Cairo	+30.0571 / +31.2272	22
//		Equatorial Guinea	Malabo (on Bioko)	+3.7523 / +8.7741	107
//		Eritrea	Asmara	+15.3315 / +38.9183	2363
//		Ethiopia	Addis Ababa / Addis Abeba	+9.0084 / +38.7575	2362
//		Gabon	Libreville	+0.3858 / +9.4496	0
//		Gambia	Banjul	+13.4399 / -16.6775	0
//		Ghana	Accra	+5.5401 / -0.2074	98
//		Guinea	Conakry	+9.5370 / -13.6785	0
//		Guinea-Bissau	Bissau	+11.8598 / -15.5875	0
//		Kenya	Nairobi	-1.2762 / +36.7965	1728
//		Lesotho	Maseru	-29.2976 / +27.4854	1673
//		Liberia	Monrovia	+6.3106 / -10.8047	0
//		Libya	Tarabulus / Tripoli	+32.8830 / +13.1897	6
//		Madagascar	Antananarivo	-18.9201 / +47.5237	1288
//		Malawi	Lilongwe	-13.9899 / +33.7703	1024
//		Mali	Bamako	+12.6530 / -7.9864	349
//		Mauritania	Nouakchott	+18.0669 / -15.9900	6
//		Mauritius	Port Louis	-20.1654 / +57.4896	134
//		Morocco	Rabat	+33.9905 / -6.8704	53
//		Mozambique	Maputo	-25.9686 / +32.5804	63
//		Namibia	Windhoek	-22.5749 / +17.0805	1721
//		Niger	Niamey	+13.5164 / +2.1157	207
//		Nigeria	Abuja	+9.0580 / +7.4891	777
//		Rwanda	Kigali	-1.9441 / +30.0619	1567
//		Sao Tome and Principe	Sao Tome (on Sao Tome)	+0.3360 / +6.7311	141
//		Senegal	Dakar	+14.6953 / -17.4439	37
//		Seychelles	Victoria (on Mahe)	-4.6167 / +55.4500	0
//		Sierra Leone	Freetown	+8.4697 / -13.2659	76
//		Somalia	Muqdisho / Mogadishu	+2.0411 / +45.3426	28
//		South Africa	Pretoria	-25.7463 / +28.1876	1271
//		Sudan	Al Khartum / Khartoum	+15.6331 / +32.5330	377
//		Swaziland	Mbabane	-26.3186 / +31.1410	1243
//		Tanzania	Dodoma	-6.1670 / +35.7497	1148
//		Togo	Lome	+6.1228 / +1.2255	63
//		Tunisia	Tunis	+36.8117 / +10.1761	0
//		Uganda	Kampala	+0.3133 / +32.5714	1202
//		Zambia	Lusaka	-15.4145 / +28.2809	1270
//		Zimbabwe	Harare	-17.8227 / +31.0496	1480
//		Mayotte (FR)	Mamoudzou	-12.7806 / +45.2278	0
//		Reunion (FR)	Saint Denis	-20.8732 / +55.4603	112
//		St. Helena (UK)	Jamestown	-15.9244 / -5.7181	292
//		Western Sahara (MA)	Laayoune / El Aaiun	+27.1536 / -13.2033	72
//
//		Oceanic States (14+12)	Oceanic Capitals	Coordinates (φ / λ)	Alt. / m
//		Australia	Canberra	-35.2820 / +149.1286	605
//		Fiji	Suva (on Viti Levu)	-18.1416 / +178.4419	0
//		Kiribati	Bairiki (on Tarawa)	+1.3282 / +172.9784	0
//		Marshall Islands	Dalap-Uliga-Darrit (on Majuro)	+7.1167 / +171.3667	0
//		Micronesia (Federated States)	Palikir (on Pohnpei)	+6.9177 / +158.1854	207
//		Nauru	Yaren	-0.5434 / +166.9196	9
//		New Zealand	Wellington	-41.2865 / +174.7762	20
//		Palau	Melekeok (on Babelthuap)	+7.5007 / +134.6241	0
//		Papua New Guinea	Port Moresby	-9.4656 / +147.1969	39
//		Samoa	Apia (on Upolu)	-13.8314 / -171.7518	0
//		Solomon Islands	Honiara (on Guadalcanal)	-9.4333 / +159.9500	29
//		Tonga	Nuku'alofa (on Tongatapu)	-21.1360 / -175.2164	0
//		Tuvalu	Vaiaku (on Funafuti)	-8.5210 / +179.1983	0
//		Vanuatu	Port Vila (on Efate)	-17.7404 / +168.3210	0
//		American Samoa (US)	Pago Pago (on Tutuila)	-14.2793 / -170.7009	49
//		Christmas Island (AU)	The Settlement / Flying Fish Cove	-10.4286 / +105.6807	0
//		Cocos Islands (AU)	Pulu Panjang / West Island	-12.1869 / +96.8283	0
//		Cook Islands (NZ)	Avarua (on Rarotonga)	-21.2039 / -159.7658	208
//		French Polynesia (FR)	Papeete (on Tahiti)	-17.5350 / -149.5696	59
//		Guam (US)	Hagatna	+13.4667 / +144.7470	9
//		New Caledonia (FR)	Noumea (on Grande Terre)	-22.2758 / +166.4581	0
//		Niue (NZ)	Alofi	-19.0565 / -169.9237	6
//		Norfolk Island (AU)	Kingston	-29.0545 / +167.9666	0
//		Northern Mariana Islands (US)	Garapan (on Saipan)	+15.2069 / +145.7197	132
//		Pitcairn Islands (UK)	Adamstown (on Pitcairn)	-25.0662 / -130.1027	0
//		Wallis and Futuna (FR)	Mata-Utu (on Wallis)	-13.2784 / -176.1430	0
//
//
//		United States of America (without District of Columbia)
//
//		States (50)	Capitals	Coordinates (φ / λ)	Alt. / m
//		Alabama	Montgomery	+32.3754 / -86.2996	75
//		Alaska	Juneau	+58.3637 / -134.5721	0
//		Arizona	Phoenix	+33.4483 / -112.0738	342
//		Arkansas	Little Rock	+34.7244 / -92.2789	91
//		California	Sacramento	+38.5737 / -121.4871	4
//		Colorado	Denver	+39.7551 / -104.9881	1613
//		Connecticut	Hartford	+41.7665 / -72.6732	13
//		Delaware	Dover	+39.1615 / -75.5136	5
//		Florida	Tallahassee	+30.4382 / -84.2806	53
//		Georgia	Atlanta	+33.7545 / -84.3897	304
//		Hawaii	Honolulu (on Oahu)	+21.2920 / -157.8219	34
//		Idaho	Boise	+43.6021 / -116.2125	834
//		Illinois	Springfield	+39.8018 / -89.6533	182
//		Indiana	Indianapolis	+39.7670 / -86.1563	221
//		Iowa	Des Moines	+41.5888 / -93.6203	267
//		Kansas	Topeka	+39.0474 / -95.6815	273
//		Kentucky	Frankfort	+38.1894 / -84.8715	151
//		Louisiana	Baton Rouge	+30.4493 / -91.1882	14
//		Maine	Augusta	+44.3294 / -69.7323	20
//		Maryland	Annapolis	+38.9693 / -76.5197	0
//		Massachusetts	Boston	+42.3589 / -71.0568	2
//		Michigan	Lansing	+42.7336 / -84.5466	257
//		Minnesota	Saint Paul	+44.9446 / -93.1027	228
//		Mississippi	Jackson	+32.3122 / -90.1780	80
//		Missouri	Jefferson City	+38.5698 / -92.1941	181
//		Montana	Helena	+46.5911 / -112.0205	1262
//		Nebraska	Lincoln	+40.8136 / -96.7026	379
//		Nevada	Carson City	+39.1501 / -119.7519	1462
//		New Hampshire	Concord	+43.2314 / -71.5597	84
//		New Jersey	Trenton	+40.2202 / -74.7642	15
//		New Mexico	Santa Fe	+35.6816 / -105.9381	2152
//		New York	Albany	+42.6517 / -73.7551	20
//		North Carolina	Raleigh	+35.7797 / -78.6434	90
//		North Dakota	Bismarck	+46.8084 / -100.7694	516
//		Ohio	Columbus	+39.9622 / -83.0007	234
//		Oklahoma	Oklahoma City	+35.4931 / -97.4591	359
//		Oregon	Salem	+44.9370 / -123.0272	58
//		Pennsylvania	Harrisburg	+40.2740 / -76.8849	108
//		Rhode Island	Providence	+41.8270 / -71.4087	2
//		South Carolina	Columbia	+34.0007 / -81.0353	84
//		South Dakota	Pierre	+44.3776 / -100.3177	442
//		Tennesee	Nashville	+36.1589 / -86.7821	138
//		Texas	Austin	+30.2687 / -97.7452	140
//		Utah	Salt Lake City	+40.7716 / -111.8882	1308
//		Vermont	Montpelier	+44.2627 / -72.5716	182
//		Virginia	Richmond	+37.5408 / -77.4339	48
//		Washington	Olympia	+47.0449 / -122.9016	27
//		West Virginia	Charleston	+38.3533 / -81.6354	174
//		Wisconsin	Madison	+43.0632 / -89.4007	267
//		Wyoming	Cheyenne	+41.1389 / -104.8165	1856
	};
}
