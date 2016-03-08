/*                     __                                               *\
**     ________ ___   / /  ___      __ ____  Scala.js Test Suite        **
**    / __/ __// _ | / /  / _ | __ / // __/  (c) 2013, LAMP/EPFL        **
**  __\ \/ /__/ __ |/ /__/ __ |/_// /_\ \    http://scala-js.org/       **
** /____/\___/_/ |_/____/_/ | |__/ /____/                               **
**                          |/____/                                     **
\*                                                                      */
package org.scalajs.testsuite.jsinterop

import scala.language.implicitConversions

import scala.scalajs.js
import scala.scalajs.runtime.RuntimeLong

import org.scalajs.jasmine.JasmineExpectation
import org.scalajs.jasminetest.{JasmineTest, TestSuiteContext}

import org.scalajs.testsuite.utils.ExpectExceptions

import scala.util.Try

/**
 * test the runtime Long implementation directly
 * does not depend on magic compiler Long rewriting
 */
object RuntimeLongTest extends JasmineTest with ExpectExceptions {
  import RuntimeLong.fromDouble

  // Ask Jasmine to compare Longs by their pair of (lo, hi)

  implicit def long2any(x: RuntimeLong): js.Any = js.Array(x.lo, x.hi)

  // Short builders

  def lg(lo: Int, hi: Int): RuntimeLong = new RuntimeLong(lo, hi)

  def lg(i: Int): RuntimeLong = new RuntimeLong(i)

  // Common values
  val MaxVal = lg(0xffffffff, 0x7fffffff)
  val MinVal = lg(0, 0x80000000)
  val IntMaxVal = lg(Int.MaxValue)
  val IntMinVal = lg(Int.MinValue)
  val IntMaxValPlus1 = lg(0x80000000, 0)
  val IntMinValMinus1 = lg(2147483647, -1)
  val MaxSafeDouble = lg(-1, 2097151)
  val TwoPow53 = lg(0, 2097152)
  val MinSafeDouble = lg(1, -2097152)
  val NegTwoPow53 = lg(0, -2097152)

  describe("scala.scalajs.runtime.RuntimeLong") {
    it("sanity of equality tests") {
      expect(lg(123, 456)).toEqual(js.Array(123, 456))
      expect(lg(123, 456)).toEqual(lg(123, 456))

      expect(lg(123, 456)).not.toEqual(js.Array(123, 4))
      expect(lg(123, 456)).not.toEqual(lg(123, 4))
      expect(lg(123, 456)).not.toEqual(js.Array(1, 456))
      expect(lg(123, 456)).not.toEqual(lg(1, 456))
      expect(lg(123, 456)).not.toEqual(123)
    }

    it("equals(Any)") {
      expect(lg(0, 0).equals(0: Any)).toBeFalsy
      expect(lg(0, 0).equals(null: Any)).toBeFalsy

      expect(lg(0, 0).equals(lg(0, 0): Any)).toBeTruthy
      expect(lg(123, 456).equals(lg(123, 456): Any)).toBeTruthy
      expect(lg(-123, 456).equals(lg(-123, 456): Any)).toBeTruthy
      expect(lg(-123, -456).equals(lg(-123, -456): Any)).toBeTruthy

      expect(lg(123, 456).equals(lg(-123, 456): Any)).toBeFalsy
      expect(lg(123, 456).equals(lg(123, -456): Any)).toBeFalsy
      expect(lg(-123, -456).equals(lg(123, -456): Any)).toBeFalsy
      expect(lg(-123, -456).equals(lg(-123, 456): Any)).toBeFalsy
    }

    it("hashCode(), as specified in j.l.Long") {
      expect(lg(0).hashCode()).toEqual(0)
      expect(lg(-1).hashCode()).toEqual(0)
      expect(lg(55).hashCode()).toEqual(55)
      expect(lg(-12).hashCode()).toEqual(11)
      expect(lg(10006548).hashCode()).toEqual(10006548)
      expect(lg(-1098748).hashCode()).toEqual(1098747)

      expect(lg(579906195, 461662560).hashCode()).toEqual(957662195)
      expect(lg(-1403218312, 327367870).hashCode()).toEqual(-1075860794)
      expect(lg(-1152051636, -274640221).hashCode()).toEqual(1425294575)
      expect(lg(1026519507, -1379463549).hashCode()).toEqual(-1863811248)
      expect(lg(363765329, -557842270).hashCode()).toEqual(-881942797)
      expect(lg(21652572, 569942698).hashCode()).toEqual(548587254)
      expect(lg(55820229, -1281708615).hashCode()).toEqual(-1328999812)
      expect(lg(-1843678104, 89453422).hashCode()).toEqual(-1756412154)
      expect(lg(-1928579430, 1836700344).hashCode()).toEqual(-529144798)
      expect(lg(-181377900, 1335444084).hashCode()).toEqual(-1163319584)
      expect(lg(1189983760, 1032146717).hashCode()).toEqual(2070477069)
      expect(lg(-1982789145, 274636318).hashCode()).toEqual(-1718642695)
      expect(lg(-2087901827, -1945935740).hashCode()).toEqual(260982265)
      expect(lg(-1911332808, 1729620001).hashCode()).toEqual(-385578983)
      expect(lg(-1920965295, 592125278).hashCode()).toEqual(-1362397169)
      expect(lg(2017870028, 751907156).hashCode()).toEqual(1419211160)
      expect(lg(1506336851, -933796127).hashCode()).toEqual(-1851816270)
      expect(lg(-1747722429, -1855422773).hashCode()).toEqual(112959880)
      expect(lg(-2139132623, -431847873).hashCode()).toEqual(1715333902)
      expect(lg(739274932, -924496860).hashCode()).toEqual(-453690224)
      expect(lg(-1482800071, 29485338).hashCode()).toEqual(-1503679197)
      expect(lg(237609240, 2048220960).hashCode()).toEqual(1950154296)
      expect(lg(-431092385, -1623412426).hashCode()).toEqual(2037562473)
      expect(lg(2144172772, 1927987317).hashCode()).toEqual(220707473)
      expect(lg(971459211, 1217334127).hashCode()).toEqual(1902658020)
      expect(lg(-530209544, -763367967).hashCode()).toEqual(840583449)
      expect(lg(-1322671605, -902331922).hashCode()).toEqual(2065572837)
      expect(lg(1361976000, 1231329666).hashCode()).toEqual(407536450)
      expect(lg(-96547475, 1640676759).hashCode()).toEqual(-1678479110)
      expect(lg(1799144078, -936998300).hashCode()).toEqual(-1558558486)
      expect(lg(221720683, -195204411).hashCode()).toEqual(-110470482)
      expect(lg(2080474705, 1194291803).hashCode()).toEqual(992932874)
      expect(lg(-1962255291, -228903623).hashCode()).toEqual(2035378556)
      expect(lg(-1961045404, -1421226733).hashCode()).toEqual(542449527)
      expect(lg(1762001719, -96661681).hashCode()).toEqual(-1824846728)
      expect(lg(568630982, -458482587).hashCode()).toEqual(-985103709)
      expect(lg(-1237704639, -1275053966).hashCode()).toEqual(37361715)
      expect(lg(936273516, -1802824213).hashCode()).toEqual(-1555729529)
      expect(lg(-870754516, -1755138351).hashCode()).toEqual(1534845437)
      expect(lg(964079858, -332884522).hashCode()).toEqual(-715250396)
      expect(lg(1769001167, 503396434).hashCode()).toEqual(2003953821)
      expect(lg(811930233, 1365142270).hashCode()).toEqual(1631287431)
      expect(lg(-280291442, 1136496326).hashCode()).toEqual(-1393125048)
      expect(lg(439731659, 755060794).hashCode()).toEqual(926193137)
      expect(lg(-561661919, -1701561506).hashCode()).toEqual(1141998463)
      expect(lg(1556104387, 1080665841).hashCode()).toEqual(480895538)
      expect(lg(1931061917, -1099252386).hashCode()).toEqual(-849143869)
      expect(lg(2086961898, -298531087).hashCode()).toEqual(-1840233445)
      expect(lg(-1148008529, -1186490352).hashCode()).toEqual(47538111)
      expect(lg(807317094, 271251327).hashCode()).toEqual(540301593)
      expect(lg(1077071399, 826295290).hashCode()).toEqual(1903332829)
      expect(lg(781949710, -1637653074).hashCode()).toEqual(-1325859168)
      expect(lg(1778433204, -839352494).hashCode()).toEqual(-1476869146)
      expect(lg(-2038023199, -2088719372).hashCode()).toEqual(84316181)
      expect(lg(-1764916235, -1980649039).hashCode()).toEqual(524038724)
      expect(lg(-1796682086, 1148567289).hashCode()).toEqual(-794988445)
      expect(lg(-1606200144, 320886535).hashCode()).toEqual(-1285356617)
      expect(lg(755146140, 2028753842).hashCode()).toEqual(1441713710)
      expect(lg(-1851453861, -2073516593).hashCode()).toEqual(365800340)
      expect(lg(-543327214, -1587342674).hashCode()).toEqual(2130603708)
      expect(lg(506958308, -1249713021).hashCode()).toEqual(-1414171289)
      expect(lg(-2097389477, 1923820719).hashCode()).toEqual(-262714124)
      expect(lg(-374932306, -523558320).hashCode()).toEqual(158195454)
      expect(lg(-902905695, -925752196).hashCode()).toEqual(50128093)
      expect(lg(-397013030, 646399757).hashCode()).toEqual(-825145129)
      expect(lg(1764398539, -956440075).hashCode()).toEqual(-1344834498)
      expect(lg(-1750710329, 1852419689).hashCode()).toEqual(-103814738)
      expect(lg(-1664538473, 864969320).hashCode()).toEqual(-1354282241)
      expect(lg(-500471847, -1312439708).hashCode()).toEqual(1408148925)
      expect(lg(14748928, 1899600418).hashCode()).toEqual(1910019874)
      expect(lg(-1985642880, -431011584).hashCode()).toEqual(1877620608)
      expect(lg(494530531, -200582329).hashCode()).toEqual(-378358620)
      expect(lg(-2067225228, -1718331081).hashCode()).toEqual(492633155)
      expect(lg(-1799546135, 897340901).hashCode()).toEqual(-1581166836)
      expect(lg(25821759, 200092463).hashCode()).toEqual(174532880)
      expect(lg(403690141, -1032813241).hashCode()).toEqual(-629188646)
      expect(lg(-1843541251, -308529236).hashCode()).toEqual(2139225425)
      expect(lg(1643311840, 1780391559).hashCode()).toEqual(200043623)
      expect(lg(1531597671, 764172997).hashCode()).toEqual(1992690082)
      expect(lg(638938496, 182932582).hashCode()).toEqual(754072038)
      expect(lg(309356043, -440275494).hashCode()).toEqual(-139359279)
      expect(lg(-541225182, 1128039519).hashCode()).toEqual(-1669264515)
      expect(lg(-387355169, -378598204).hashCode()).toEqual(25583899)
      expect(lg(1787244135, 103129337).hashCode()).toEqual(1822592670)
      expect(lg(-1654639624, -890602930).hashCode()).toEqual(1468680630)
      expect(lg(-1867306675, -303043235).hashCode()).toEqual(2103231504)
      expect(lg(1255224728, 265017316).hashCode()).toEqual(1159389820)
      expect(lg(119985367, 695098919).hashCode()).toEqual(776506096)
      expect(lg(-332671386, 1583817866).hashCode()).toEqual(-1303579924)
      expect(lg(1610629865, 571880320).hashCode()).toEqual(1108767081)
      expect(lg(727577343, -1794328817).hashCode()).toEqual(-1101969936)
      expect(lg(730759795, -394092436).hashCode()).toEqual(-1022615009)
      expect(lg(-148400203, 1074931585).hashCode()).toEqual(-1221218252)
      expect(lg(181091802, 314250080).hashCode()).toEqual(410005178)
      expect(lg(-1934827635, -889463837).hashCode()).toEqual(1180107886)
      expect(lg(-1067099255, -650316777).hashCode()).toEqual(425308062)
      expect(lg(1821917070, 174468125).hashCode()).toEqual(1727927187)
      expect(lg(474121453, -830281051).hashCode()).toEqual(-759140792)
      expect(lg(-402668999, -2100801229).hashCode()).toEqual(1698140938)
      expect(lg(-615008378, -976157749).hashCode()).toEqual(512144461)
    }

    it("toString()") {
      expect(lg(0).toString()).toEqual("0")
      expect(lg(1).toString()).toEqual("1")
      expect(lg(-1).toString()).toEqual("-1")
      expect(IntMaxVal.toString()).toEqual(Int.MaxValue.toString())
      expect(IntMaxValPlus1.toString()).toEqual("2147483648")
      expect(IntMinVal.toString()).toEqual(Int.MinValue.toString())
      expect(IntMinValMinus1.toString()).toEqual("-2147483649")
      expect(lg(999999999).toString()).toEqual("999999999")
      expect(lg(1000000000).toString()).toEqual("1000000000")
      expect(MaxSafeDouble.toString()).toEqual("9007199254740991")
      expect(TwoPow53.toString()).toEqual("9007199254740992")
      expect(MinSafeDouble.toString()).toEqual("-9007199254740991")
      expect(NegTwoPow53.toString()).toEqual("-9007199254740992")

      expect(lg(-86922, -1).toString()).toEqual("-86922")
      expect(lg(0, 0).toString()).toEqual("0")
      expect(lg(-21874015, -1).toString()).toEqual("-21874015")
      expect(lg(1317110830, -489).toString()).toEqual("-2098921896914")
      expect(lg(-698060208, 18855).toString()).toEqual("80985205273168")
      expect(lg(858389071, -2899145).toString()).toEqual("-12451732102972849")
      expect(lg(3350, 0).toString()).toEqual("3350")
      expect(lg(2005360390, -21540).toString()).toEqual("-92511590195450")
      expect(lg(-2, -1).toString()).toEqual("-2")
      expect(lg(1492984294, 103900277).toString())
        .toEqual("446248293253325286")
      expect(lg(116015740, 116321286).toString()).toEqual("499596119314678396")
      expect(lg(-3205893, -1).toString()).toEqual("-3205893")
      expect(lg(1988813462, -20667).toString()).toEqual("-88762100292970")
      expect(lg(-1278004, -1).toString()).toEqual("-1278004")
      expect(lg(-1, -1).toString()).toEqual("-1")
      expect(lg(-305393, -1).toString()).toEqual("-305393")
      expect(lg(-2, -1).toString()).toEqual("-2")
      expect(lg(-1678336113, 18695185).toString()).toEqual("80295210784300943")
      expect(lg(5, 0).toString()).toEqual("5")
      expect(lg(21, 0).toString()).toEqual("21")
      expect(lg(64, 0).toString()).toEqual("64")
      expect(lg(39146094, 0).toString()).toEqual("39146094")
      expect(lg(-1725731, -1).toString()).toEqual("-1725731")
      expect(lg(-874655652, -178824949).toString())
        .toEqual("-768047304243556260")
      expect(lg(380990122, -635).toString()).toEqual("-2726923242838")
      expect(lg(1318520807, -415).toString()).toEqual("-1781092907033")
      expect(lg(-213275, -1).toString()).toEqual("-213275")
      expect(lg(184176746, 1784).toString()).toEqual("7662405832810")
      expect(lg(460945549, -36).toString()).toEqual("-154157877107")
      expect(lg(1586508389, -216524094).toString())
        .toEqual("-929963900939521435")
      expect(lg(-6872, -1).toString()).toEqual("-6872")
      expect(lg(-333987816, 7413).toString()).toEqual("31842553544728")
      expect(lg(-1817926382, 132147).toString()).toEqual("567569520305426")
      expect(lg(19649016, 0).toString()).toEqual("19649016")
      expect(lg(-1349346, -1).toString()).toEqual("-1349346")
      expect(lg(-1372338764, 2207193).toString()).toEqual("9479824673588660")
      expect(lg(3521781, 0).toString()).toEqual("3521781")
      expect(lg(1740, 0).toString()).toEqual("1740")
      expect(lg(0, 0).toString()).toEqual("0")
      expect(lg(-1654582044, 21).toString()).toEqual("92834698468")
      expect(lg(100400158, -18659001).toString()).toEqual("-80139798970631138")
      expect(lg(30058, 0).toString()).toEqual("30058")
      expect(lg(1332815438, -142265).toString()).toEqual("-611022189550002")
      expect(lg(472694602, 119894).toString()).toEqual("514941281681226")
      expect(lg(-1962042949, 571).toString()).toEqual("2454759250363")
      expect(lg(1595551038, 3459895).toString()).toEqual("14860137468144958")
      expect(lg(-79255, -1).toString()).toEqual("-79255")
      expect(lg(-1501556660, 533210).toString()).toEqual("2290122305310796")
      expect(lg(-463451414, -175936602).toString())
        .toEqual("-755641947927852310")
      expect(lg(-771329970, -610447526).toString())
        .toEqual("-2621852156570472370")
      expect(lg(698569929, -9).toString()).toEqual("-37956135735")
      expect(lg(853219, 0).toString()).toEqual("853219")
      expect(lg(901, 0).toString()).toEqual("901")
      expect(lg(434694682, 1021).toString()).toEqual("4385596303898")
      expect(lg(-972597865, -1).toString()).toEqual("-972597865")
      expect(lg(-8057379, -1).toString()).toEqual("-8057379")
      expect(lg(-14968, -1).toString()).toEqual("-14968")
      expect(lg(-98204964, -1).toString()).toEqual("-98204964")
      expect(lg(335479, 0).toString()).toEqual("335479")
      expect(lg(54810714, -100).toString()).toEqual("-429441918886")
      expect(lg(9798741, 0).toString()).toEqual("9798741")
      expect(lg(-896875642, 31643665).toString()).toEqual("135908509698671494")
      expect(lg(233027789, -32851335).toString())
        .toEqual("-141095409221912371")
      expect(lg(-359183840, -2104985).toString()).toEqual("-9040837797787104")
      expect(lg(-889, -1).toString()).toEqual("-889")
      expect(lg(-1072884302, 0).toString()).toEqual("3222082994")
      expect(lg(-1454853, -1).toString()).toEqual("-1454853")
      expect(lg(-2113969463, 127).toString()).toEqual("547641844425")
      expect(lg(-1766834443, 0).toString()).toEqual("2528132853")
      expect(lg(242, 0).toString()).toEqual("242")
      expect(lg(-1655763891, -1).toString()).toEqual("-1655763891")
      expect(lg(82, 0).toString()).toEqual("82")
      expect(lg(-120254181, -1).toString()).toEqual("-120254181")
      expect(lg(-210088, -1).toString()).toEqual("-210088")
      expect(lg(-2, -1).toString()).toEqual("-2")
      expect(lg(598888267, 58267).toString()).toEqual("250255458324299")
      expect(lg(-100656997, -1).toString()).toEqual("-100656997")
      expect(lg(1672622015, -6).toString()).toEqual("-24097181761")
      expect(lg(206088, 0).toString()).toEqual("206088")
      expect(lg(-593, -1).toString()).toEqual("-593")
      expect(lg(-99542049, -1).toString()).toEqual("-99542049")
      expect(lg(421501, 0).toString()).toEqual("421501")
      expect(lg(-2, -1).toString()).toEqual("-2")
      expect(lg(-101, -1).toString()).toEqual("-101")
      expect(lg(3, 0).toString()).toEqual("3")
      expect(lg(2082590966, 3).toString()).toEqual("14967492854")
      expect(lg(-86853659, -355870).toString()).toEqual("-1528445803513883")
      expect(lg(-1353126070, 6230).toString()).toEqual("26760588095306")
      expect(lg(1576139368, 2899).toString()).toEqual("12452686330472")
      expect(lg(1022479965, -30415).toString()).toEqual("-130630407827875")
      expect(lg(-1691843023, -3).toString()).toEqual("-10281777615")
      expect(lg(2013284571, -21071).toString()).toEqual("-90497242609445")
      expect(lg(1990158591, -3245).toString()).toEqual("-13935178716929")
      expect(lg(-11308540, -1).toString()).toEqual("-11308540")
      expect(lg(545166, 0).toString()).toEqual("545166")
      expect(lg(1778574369, -243007).toString()).toEqual("-1043705339124703")
      expect(lg(510, 0).toString()).toEqual("510")
      expect(lg(1809514269, -1).toString()).toEqual("-2485453027")
      expect(lg(-15103, -1).toString()).toEqual("-15103")
      expect(lg(-779514418, -39296382).toString())
        .toEqual("-168776672025670194")
    }

    it("toByte") {
      expect(lg(0).toByte).toEqual(0)
      expect(lg(-1).toByte).toEqual(-1)
      expect(lg(0xfedcba98, 0x76543210).toByte).toEqual(0x98.toByte)

      expect(lg(-1755353242, -1245269156).toByte).toEqual(102)
      expect(lg(-359135667, 1391746928).toByte).toEqual(77)
      expect(lg(-957203503, 1516742479).toByte).toEqual(-47)
      expect(lg(-1928741654, 1162703256).toByte).toEqual(-22)
      expect(lg(-1698228849, 1497186951).toByte).toEqual(-113)
      expect(lg(-68041812, -2115448390).toByte).toEqual(-84)
      expect(lg(1534301729, 1468418695).toByte).toEqual(33)
      expect(lg(1101829489, -514588123).toByte).toEqual(113)
      expect(lg(-1437577204, 1896338488).toByte).toEqual(12)
      expect(lg(-857671082, -1304076936).toByte).toEqual(86)
      expect(lg(-292818212, -1485650549).toByte).toEqual(-36)
      expect(lg(1044510040, 147719255).toByte).toEqual(88)
      expect(lg(-1166136469, 78076997).toByte).toEqual(107)
      expect(lg(500131901, 248541787).toByte).toEqual(61)
      expect(lg(1863435363, -1465266670).toByte).toEqual(99)
      expect(lg(136483252, 1662447178).toByte).toEqual(-76)
      expect(lg(1787939584, 1303926235).toByte).toEqual(0)
      expect(lg(2105657787, 845433223).toByte).toEqual(-69)
      expect(lg(-1298285542, -1826340261).toByte).toEqual(26)
      expect(lg(-766959552, -326327606).toByte).toEqual(64)
    }

    it("toShort") {
      expect(lg(0).toShort).toEqual(0)
      expect(lg(-1).toShort).toEqual(-1)
      expect(lg(0xfedcba98, 0x76543210).toShort).toEqual(0xba98.toShort)

      expect(lg(1925512546, -812328457).toShort).toEqual(-670)
      expect(lg(2028716555, -1639243756).toShort).toEqual(-15861)
      expect(lg(-1970657557, -1904990267).toShort).toEqual(9963)
      expect(lg(-1012119590, -1704668195).toShort).toEqual(18394)
      expect(lg(848486636, -810351120).toShort).toEqual(-7956)
      expect(lg(2103989197, 955793808).toShort).toEqual(21453)
      expect(lg(-237938237, -703399620).toShort).toEqual(22979)
      expect(lg(666247428, -1109641927).toShort).toEqual(8452)
      expect(lg(1824561213, -872828437).toShort).toEqual(-26563)
      expect(lg(-10950266, -1779965318).toShort).toEqual(-5754)
      expect(lg(1251814932, -491043391).toShort).toEqual(11796)
      expect(lg(-117750172, -366379322).toShort).toEqual(18020)
      expect(lg(-2095575368, 965048164).toShort).toEqual(3768)
      expect(lg(-177410531, 1454361289).toShort).toEqual(-4579)
      expect(lg(-359035310, -790126871).toShort).toEqual(-29102)
      expect(lg(1486058820, 1675509542).toShort).toEqual(30020)
      expect(lg(268881157, -342358099).toShort).toEqual(-13051)
      expect(lg(-1089211040, 747294820).toShort).toEqual(-2720)
      expect(lg(1163661942, 1708185440).toShort).toEqual(4726)
      expect(lg(-1363821038, -1952481751).toShort).toEqual(-16878)
    }

    it("toInt") {
      expect(lg(0).toInt).toEqual(0)
      expect(lg(-1).toInt).toEqual(-1)
      expect(lg(0xfedcba98, 0x76543210).toInt).toEqual(0xfedcba98)

      expect(lg(-1869423218, -5516698).toInt).toEqual(-1869423218)
      expect(lg(450655357, -521592408).toInt).toEqual(450655357)
      expect(lg(-596464514, 629510497).toInt).toEqual(-596464514)
      expect(lg(1668957409, 1231040344).toInt).toEqual(1668957409)
      expect(lg(-313016061, 283507721).toInt).toEqual(-313016061)
      expect(lg(-406779255, 1389322213).toInt).toEqual(-406779255)
      expect(lg(-1125423893, -436921025).toInt).toEqual(-1125423893)
      expect(lg(1491309031, 948401259).toInt).toEqual(1491309031)
      expect(lg(360542935, -1033853853).toInt).toEqual(360542935)
      expect(lg(178673916, -2045867551).toInt).toEqual(178673916)
      expect(lg(-1167644863, 738699232).toInt).toEqual(-1167644863)
      expect(lg(-1852739075, 950841298).toInt).toEqual(-1852739075)
      expect(lg(-1965326912, 1694989583).toInt).toEqual(-1965326912)
      expect(lg(-141857741, -1197558189).toInt).toEqual(-141857741)
      expect(lg(-938893686, 1763555645).toInt).toEqual(-938893686)
      expect(lg(-1178638558, 299067184).toInt).toEqual(-1178638558)
      expect(lg(-1296424902, -1694453755).toInt).toEqual(-1296424902)
      expect(lg(204387309, -240738711).toInt).toEqual(204387309)
      expect(lg(-942136876, -527367452).toInt).toEqual(-942136876)
      expect(lg(-1703892744, 240186844).toInt).toEqual(-1703892744)
    }

    it("toLong") {
      expect(lg(0).toLong == 0L).toBeTruthy
      expect(lg(-1).toLong == - 1L).toBeTruthy
      expect(lg(0xfedcba98, 0x76543210).toLong == 0x76543210fedcba98L).toBeTruthy

      expect(lg(-85753595, 1608259083).toLong == 6907420169189163269L).toBeTruthy
      expect(lg(539593679, -1527121853).toLong == - 6558938415102325809L).toBeTruthy
      expect(lg(-379998034, -1777303946).toLong == - 7633462319206780754L).toBeTruthy
      expect(lg(-655641274, -943321249).toLong == - 4051533910437546682L).toBeTruthy
      expect(lg(1727460259, -905790147).toLong == - 3890339056676572253L).toBeTruthy
      expect(lg(1824805856, -719806090).toLong == - 3091543614186826784L).toBeTruthy
      expect(lg(948567983, 653384746).toLong == 2806266116723834799L).toBeTruthy
      expect(lg(-957910924, -405401095).toLong == - 1741184441450532748L).toBeTruthy
      expect(lg(-433042213, 790675337).toLong == 3395924718030703835L).toBeTruthy
      expect(lg(889526541, -1795647094).toLong == - 7712245542997911283L).toBeTruthy
      expect(lg(1316066543, -640532153).toLong == - 2751064647855401745L).toBeTruthy
      expect(lg(1913378322, 1216751901).toLong == 5225909624054208018L).toBeTruthy
      expect(lg(-434813127, 310602037).toLong == 1334025594846136121L).toBeTruthy
      expect(lg(1689963942, -366687109).toLong == - 1574909139329823322L).toBeTruthy
      expect(lg(754250892, -2128587091).toLong == - 9142211941778525044L).toBeTruthy
      expect(lg(-1817691823, -1284620305).toLong == - 5517402195275269807L).toBeTruthy
      expect(lg(-222627957, 1772466007).toLong == 7612683537409046411L).toBeTruthy
      expect(lg(-1282993697, -688214725).toLong == - 2955859733488660001L).toBeTruthy
      expect(lg(799857959, 107587404).toLong == 462084382441397543L).toBeTruthy
      expect(lg(2076251528, 2049295309).toLong == 8801656334077465992L).toBeTruthy
    }

    when("strict-floats").it("toFloat (strict)") {
      expect(lg(0).toFloat).toEqual(0)
      expect(lg(-1).toFloat).toEqual(-1)

      if (!TestSuiteContext.hasTag("fullopt-stage")) {
        expect(MaxVal.toFloat).toEqual(9.223372E18f)
        expect(MinVal.toFloat).toEqual(-9.223372E18f)
      } else {
        // Closure seems to incorrectly rewrite the constant on the right :-(
        expect(MaxVal.toFloat).toBeCloseTo(9.223372E18f, -4)
        expect(MinVal.toFloat).toBeCloseTo(-9.223372E18f, -4)
      }

      expect(lg(-1026388143, 1116923232).toFloat).toEqual(4.7971489E18f)
      expect(lg(-1288678667, -521651607).toFloat).toEqual(-2.24047663E18f)
      expect(lg(1192262605, 1069184891).toFloat).toEqual(4.59211416E18f)
      expect(lg(-180353617, 789161022).toFloat).toEqual(3.38942079E18f)
      expect(lg(-1158443188, -1585038363).toFloat).toEqual(-6.8076878E18f)
      expect(lg(906981906, 1726665521).toFloat).toEqual(7.4159717E18f)
      expect(lg(2042933575, -431379283).toFloat).toEqual(-1.85275997E18f)
      expect(lg(599900903, 1335148382).toFloat).toEqual(5.7344188E18f)
      expect(lg(1458166084, 746013039).toFloat).toEqual(3.20410168E18f)
      expect(lg(1956524672, -1683605603).toFloat).toEqual(-7.2310311E18f)
      expect(lg(478583639, 1796320118).toFloat).toEqual(7.7151362E18f)
      expect(lg(-1645816617, 329141676).toFloat).toEqual(1.41365268E18f)
      expect(lg(184187116, -705937657).toFloat).toEqual(-3.03197918E18f)
      expect(lg(659513335, -941305424).toFloat).toEqual(-4.04287594E18f)
      expect(lg(770505156, -1820844549).toFloat).toEqual(-7.8204678E18f)
      expect(lg(929928858, -1390767911).toFloat).toEqual(-5.9733025E18f)
      expect(lg(-1475096259, 262207373).toFloat).toEqual(1.1261721E18f)
      expect(lg(787691795, 933383012).toFloat).toEqual(4.00884963E18f)
      expect(lg(1189057493, -334139018).toFloat).toEqual(-1.43511611E18f)
      expect(lg(-618946450, 888051141).toFloat).toEqual(3.81415059E18f)
    }

    it("toDouble") {
      expect(lg(0).toDouble).toEqual(0)
      expect(lg(-1).toDouble).toEqual(-1)

      if (!TestSuiteContext.hasTag("fullopt-stage")) {
        expect(MaxVal.toDouble).toEqual(9.223372036854776E18)
        expect(MinVal.toDouble).toEqual(-9.223372036854776E18)
      } else {
        // Closure seems to incorrectly rewrite the constant on the right :-(
        expect(MaxVal.toDouble).toBeCloseTo(9.223372036854776E18, -4)
        expect(MinVal.toDouble).toBeCloseTo(-9.223372036854776E18, -4)
      }

      expect(lg(-151011088, 797216310).toDouble).toEqual(3.4240179834317537E18)
      expect(lg(-508205099, 19929381).toDouble).toEqual(8.5596043411285968E16)
      expect(lg(1249322201, -736451403).toDouble)
        .toEqual(-3.1630346897289943E18)
      expect(lg(483575860, -1044191477).toDouble)
        .toEqual(-4.4847682439933604E18)
      expect(lg(-1526343930, -149046007).toDouble)
        .toEqual(-6.4014772289576371E17)
      expect(lg(531728928, -412036011).toDouble)
        .toEqual(-1.76968119148756736E18)
      expect(lg(-734111585, -1993185640).toDouble)
        .toEqual(-8.5606671350959739E18)
      expect(lg(-1407864332, -2104881296).toDouble)
        .toEqual(-9.0403963253949932E18)
      expect(lg(-1712351423, -1513137310).toDouble)
        .toEqual(-6.4988752582247977E18)
      expect(lg(1969244733, -181115448).toDouble)
        .toEqual(-7.7788492399114394E17)
      expect(lg(-907683842, 1777829016).toDouble)
        .toEqual(7.6357174849871442E18)
      expect(lg(-815927209, 291826806).toDouble)
        .toEqual(1.25338659134517658E18)
      expect(lg(463523496, -742968207).toDouble)
        .toEqual(-3.1910241505692349E18)
      expect(lg(1482622807, 1727987781).toDouble)
        .toEqual(7.4216510087652332E18)
      expect(lg(1170040143, -1906661060).toDouble)
        .toEqual(-8.189046896086654E18)
      expect(lg(-85609173, 1590612176).toDouble).toEqual(6.8316272807487539E18)
      expect(lg(-1212811257, -1876873801).toDouble)
        .toEqual(-8.0611115909320561E18)
      expect(lg(-648802816, 398781194).toDouble).toEqual(1.7127521901359959E18)
      expect(lg(-1484519186, -1500419423).toDouble)
        .toEqual(-6.4442523492577423E18)
      expect(lg(-2016996893, -398756124).toDouble)
        .toEqual(-1.71264450938175027E18)
    }

    it("comparisons") {

      def test(x: RuntimeLong, y: RuntimeLong, expected: Int): Unit = {
        expect(x.compareTo(y).signum).toEqual(expected)
        expect(x.compareTo(y.toLong: java.lang.Long).signum).toEqual(expected)
        expect(x.equals(y)).toBe(expected == 0)
        expect(x.notEquals(y)).toBe(expected != 0)
        expect(x < y).toBe(expected < 0)
        expect(x <= y).toBe(expected <= 0)
        expect(x > y).toBe(expected > 0)
        expect(x >= y).toBe(expected >= 0)
      }

      test(lg(0), lg(0), 0)
      test(lg(0), lg(1), -1)
      test(lg(0), lg(-1), 1)
      test(MaxVal, MinVal, 1)
      test(MinVal, MaxVal, -1)

      // Positive and negative numbers requiring lo to be compared via unsigned
      test(lg(0x87654321, 0x654789ab), lg(0x12345678, 0x654789ab), 1)
      test(lg(0x87654321, 0x89abcdef), lg(0x12345678, 0x89abcdef), 1)

      // Workaround for https://code.google.com/p/v8/issues/detail?id=3304
      test(lg(-1, 0), lg(0, 0), 1)
      test(lg(0, 0), lg(-1, 0), -1)

      test(lg(173547161, -1884162399), lg(173547161, -1884162399), 0)
      test(lg(-1131022787, -472928681), lg(-1131022787, -472928681), 0)
      test(lg(-1426164191, 1230100202), lg(-1426164191, 1230100202), 0)
      test(lg(-865774626, 1656835920), lg(-865774626, 1656835920), 0)
      test(lg(323675568, -725625271), lg(323675568, -725625271), 0)
      test(lg(-480943595, -1454872354), lg(-480943595, -1454872354), 0)
      test(lg(-626788852, 1037229194), lg(-626788852, 1037229194), 0)
      test(lg(-717389653, 232764759), lg(-717389653, 232764759), 0)
      test(lg(-861190423, -1233377930), lg(-861190423, -1233377930), 0)
      test(lg(-424759090, 2081288998), lg(-424759090, 2081288998), 0)

      test(lg(-1092215366, 753517982), lg(349136582, -103427916), 1)
      test(lg(363609757, -1151024787), lg(472951646, -1802702403), 1)
      test(lg(604332601, 1869576376), lg(1642523661, 1083165388), 1)
      test(lg(309732766, 1349689861), lg(1287300335, 1464464808), -1)
      test(lg(-1309668929, -965374553), lg(-1952664258, 53355972), -1)
      test(lg(1881957750, 388099413), lg(1843907319, -1819358211), 1)
      test(lg(-969542710, 864289013), lg(-1025874755, 1102102911), -1)
      test(lg(-1425636748, -220185411), lg(1184140796, 40447497), -1)
      test(lg(242386079, 452246653), lg(435337552, -956883630), 1)
      test(lg(-1007383056, 344856628), lg(-195994328, 635205577), -1)
      test(lg(-1652098619, 2042392045), lg(819672742, -2139008380), 1)
      test(lg(1423590080, 1919857862), lg(918443721, 1202178673), 1)
      test(lg(-1726296442, 302493002), lg(314727886, 1583734481), -1)
      test(lg(-2124336701, 769721099), lg(461146322, -591528218), 1)
      test(lg(1544826993, -689540243), lg(-1107003972, -1622786326), 1)
      test(lg(2050227802, 951848379), lg(-774454951, 1675192386), -1)
      test(lg(251298779, -327163776), lg(767615943, 1531730165), -1)
      test(lg(1890888425, 761833495), lg(1870917399, 2027251288), -1)
      test(lg(594868313, 126374530), lg(-1567484882, -1199917303), 1)
      test(lg(-914360997, -703435655), lg(2049249771, -1581791194), 1)
      test(lg(-732484281, -738997306), lg(1445589646, 1910084021), -1)
      test(lg(340771740, 1351224018), lg(459324247, 1301544548), 1)
      test(lg(-940710332, 1344186742), lg(-1143672211, 1112189558), 1)
      test(lg(-804347876, 364046111), lg(-4317439, -1733157379), 1)
      test(lg(914214836, -1226397169), lg(-299522125, 1393423940), -1)
      test(lg(1244546642, 1821771770), lg(44151604, -1398558064), 1)
      test(lg(-2094640323, -1469168677), lg(-263524564, 88152070), -1)
      test(lg(-124567753, -93039352), lg(-200449699, -30383890), -1)
      test(lg(161119306, -1098626173), lg(-137189625, 1289988889), -1)
      test(lg(-2052616761, 846341515), lg(-150583666, 1044666783), -1)
      test(lg(-10359669, -1628837253), lg(165345114, 1529503183), -1)
      test(lg(1717988228, 1622548180), lg(834798590, -1907713185), 1)
      test(lg(-1416372109, -353311343), lg(-722195813, -2060788759), 1)
      test(lg(980620531, -300588346), lg(-889348218, 1805452697), -1)
      test(lg(-465681479, 556544868), lg(-684386776, 724207906), -1)
      test(lg(1720493596, 1118244444), lg(2048914469, -789300492), 1)
      test(lg(-1259678249, -1557339417), lg(-1908141376, -468055129), -1)
      test(lg(1374750478, 1591281700), lg(1107931774, 1073828802), 1)
      test(lg(1307860622, -1769647645), lg(-1521056504, 1476896409), -1)
      test(lg(1870719065, -606069057), lg(1219817813, -1063559023), 1)
      test(lg(-526519712, 1166848880), lg(-748095992, 59925642), 1)
      test(lg(-1011429486, -2053277854), lg(537284118, 1714076830), -1)
      test(lg(-669104363, -107157886), lg(1647426475, -1784147450), 1)
      test(lg(-389860398, 693324889), lg(1047633230, -1757663140), 1)
      test(lg(-200206281, 96771163), lg(613429570, -1206384633), 1)
      test(lg(-1436571081, -2050819200), lg(-665572561, 644211697), -1)
      test(lg(620796821, -567816428), lg(-109412350, -624638338), 1)
      test(lg(858464866, -2104597302), lg(-987329519, 1189618105), -1)
      test(lg(-1342634556, -1517778924), lg(-693373055, 142499537), -1)
      test(lg(1839280888, -168388422), lg(-1645740821, -1967920957), 1)
    }

    it("bitwise not ~") {
      expect(~lg(-1664374423, -327449893)).toEqual(lg(1664374422, 327449892))
      expect(~lg(2033180389, 1179462630)).toEqual(lg(-2033180390, -1179462631))
      expect(~lg(1134559213, -581653070)).toEqual(lg(-1134559214, 581653069))
      expect(~lg(304074637, 795726116)).toEqual(lg(-304074638, -795726117))
      expect(~lg(1711832786, -1153070600)).toEqual(lg(-1711832787, 1153070599))
      expect(~lg(1526506636, -966114537)).toEqual(lg(-1526506637, 966114536))
      expect(~lg(-4362924, -1155261398)).toEqual(lg(4362923, 1155261397))
      expect(~lg(1976846288, 68873333)).toEqual(lg(-1976846289, -68873334))
      expect(~lg(980717877, 1171857117)).toEqual(lg(-980717878, -1171857118))
      expect(~lg(-1087568371, -543704247)).toEqual(lg(1087568370, 543704246))
      expect(~lg(-466027719, -693030606)).toEqual(lg(466027718, 693030605))
      expect(~lg(-457333959, -1344424075)).toEqual(lg(457333958, 1344424074))
      expect(~lg(1195369387, 1211454824)).toEqual(lg(-1195369388, -1211454825))
      expect(~lg(-1637646575, -618600149)).toEqual(lg(1637646574, 618600148))
      expect(~lg(-1882417449, -81477817)).toEqual(lg(1882417448, 81477816))
      expect(~lg(755550611, 520392565)).toEqual(lg(-755550612, -520392566))
      expect(~lg(754282894, 1550447286)).toEqual(lg(-754282895, -1550447287))
      expect(~lg(-949172350, 708028074)).toEqual(lg(949172349, -708028075))
      expect(~lg(-1587810907, 1344614949)).toEqual(lg(1587810906, -1344614950))
      expect(~lg(1761617638, 353615614)).toEqual(lg(-1761617639, -353615615))
      expect(~lg(153730677, -249152221)).toEqual(lg(-153730678, 249152220))
      expect(~lg(189227913, -2071190798)).toEqual(lg(-189227914, 2071190797))
      expect(~lg(853867869, -445686069)).toEqual(lg(-853867870, 445686068))
      expect(~lg(779434874, -417640993)).toEqual(lg(-779434875, 417640992))
      expect(~lg(-1997707716, 1100729421)).toEqual(lg(1997707715, -1100729422))
      expect(~lg(-1171311730, 1236578927)).toEqual(lg(1171311729, -1236578928))
      expect(~lg(833922039, -1773972622)).toEqual(lg(-833922040, 1773972621))
      expect(~lg(-1414648870, -1222586076)).toEqual(lg(1414648869, 1222586075))
      expect(~lg(-1123832583, 1270176017)).toEqual(lg(1123832582, -1270176018))
      expect(~lg(-1163066310, -237396272)).toEqual(lg(1163066309, 237396271))
      expect(~lg(1826566062, -509270118)).toEqual(lg(-1826566063, 509270117))
      expect(~lg(450318542, -1650640100)).toEqual(lg(-450318543, 1650640099))
      expect(~lg(-1461907705, 27364748)).toEqual(lg(1461907704, -27364749))
      expect(~lg(-1012261257, -1691289855)).toEqual(lg(1012261256, 1691289854))
      expect(~lg(1929178873, -1804481537)).toEqual(lg(-1929178874, 1804481536))
      expect(~lg(888719199, 1846455122)).toEqual(lg(-888719200, -1846455123))
      expect(~lg(-984231683, 867292443)).toEqual(lg(984231682, -867292444))
      expect(~lg(-2105026706, 16146222)).toEqual(lg(2105026705, -16146223))
      expect(~lg(-1742028654, 1648876190)).toEqual(lg(1742028653, -1648876191))
      expect(~lg(-1922039595, 60702354)).toEqual(lg(1922039594, -60702355))
      expect(~lg(-264728649, -275960742)).toEqual(lg(264728648, 275960741))
      expect(~lg(-1237639033, 1761272006)).toEqual(lg(1237639032, -1761272007))
      expect(~lg(-1118919823, -901486923)).toEqual(lg(1118919822, 901486922))
      expect(~lg(-18001221, 1121574636)).toEqual(lg(18001220, -1121574637))
      expect(~lg(-2122002357, 1370943784)).toEqual(lg(2122002356, -1370943785))
      expect(~lg(-2006182036, 1422441077)).toEqual(lg(2006182035, -1422441078))
      expect(~lg(-1314896175, -460075840)).toEqual(lg(1314896174, 460075839))
      expect(~lg(-1829402919, 1031934891)).toEqual(lg(1829402918, -1031934892))
      expect(~lg(2138673172, 107590305)).toEqual(lg(-2138673173, -107590306))
      expect(~lg(-1382443515, 56307752)).toEqual(lg(1382443514, -56307753))
    }

    it("bitwise or |") {
      expect(lg(1198889513, -170491266) | lg(356560637, 1244673694))
        .toEqual(lg(1467334397, -608514))
      expect(lg(-1930990792, 627822941) | lg(-1849669008, 185716690))
        .toEqual(lg(-1645778056, 796647391))
      expect(lg(711185578, -154795743) | lg(1446469570, -104529814))
        .toEqual(lg(2121785322, -3735189))
      expect(lg(356565628, 275405582) | lg(380967239, 1356925723)).toEqual(lg(
          401988479, 1357601567))
      expect(lg(1968397619, -447093015) | lg(-1242708043, 1353146913))
        .toEqual(lg(-167780425, -167778583))
      expect(lg(-2121965024, -76133937) | lg(2104409609, -1365814226))
        .toEqual(lg(-34603479, -565777))
      expect(lg(1496398822, -548061626) | lg(-556169301, -245689186))
        .toEqual(lg(-537280529, -10535202))
      expect(lg(856203065, -1102382704) | lg(1276763344, 377524977))
        .toEqual(lg(2132402169, -1093993487))
      expect(lg(474066920, -215674305) | lg(366737695, 530830706)).toEqual(lg(
          500957183, -5777537))
      expect(lg(-1543310820, 664058893) | lg(1002387606, 1826081595))
        .toEqual(lg(-1077937506, 1876426559))
      expect(lg(1606847457, -857707283) | lg(-82108753, 628476252)).toEqual(lg(
          -2121745, -302649859))
      expect(lg(703699686, -1218298019) | lg(1575693246, -565500071))
        .toEqual(lg(2113649662, -9748643))
      expect(lg(1281663616, 1255777790) | lg(1708663964, 1604300502))
        .toEqual(lg(1845274268, 1608495102))
      expect(lg(-1315547660, 1726760037) | lg(-442781559, 235328140))
        .toEqual(lg(-174066179, 1861146349))
      expect(lg(2014986997, -1130692301) | lg(124088654, 1637408903))
        .toEqual(lg(2139059199, -40115785))
      expect(lg(1653153899, 1412277603) | lg(-1615398494, -682581111))
        .toEqual(lg(-4195861, -679630869))
      expect(lg(551077237, 1349033186) | lg(597575118, 1662855120)).toEqual(lg(
          601802239, 1937620978))
      expect(lg(613289137, -1123701660) | lg(-1383294317, 369006329))
        .toEqual(lg(-1383162189, -1107312899))
      expect(lg(-418175046, -593383309) | lg(1468132939, 360734532))
        .toEqual(lg(-141299717, -576585865))
      expect(lg(1428236018, -1294026291) | lg(572735565, 1213340152))
        .toEqual(lg(1998808831, -86066691))
      expect(lg(-1949058688, -1013245209) | lg(416580246, 300148007))
        .toEqual(lg(-1680360554, -738459673))
      expect(lg(-1746245220, 1427323605) | lg(-1185613404, -469621610))
        .toEqual(lg(-1073808964, -183288105))
      expect(lg(1445648649, 701317455) | lg(1407661733, 1287118327))
        .toEqual(lg(1475346349, 1845485055))
      expect(lg(-1861500445, 764080137) | lg(-33812527, -411163560))
        .toEqual(lg(-33566733, -268503975))
      expect(lg(-1408712806, 393166157) | lg(1323973395, 1580353248))
        .toEqual(lg(-286605413, 1602191341))
      expect(lg(-2072304578, -2142600249) | lg(-625840402, -2018265417))
        .toEqual(lg(-553947394, -2013546505))
      expect(lg(450125308, 1742298015) | lg(-999674466, -89794491)).toEqual(lg(
          -553746946, -140321))
      expect(lg(1239068904, -68194107) | lg(-1092247939, -639552609))
        .toEqual(lg(-16643, -68193313))
      expect(lg(-2075047684, -1706497393) | lg(-119858776, -1461536706))
        .toEqual(lg(-52733444, -1159005505))
      expect(lg(-1065293728, 1045575815) | lg(943802850, 4130803)).toEqual(lg(
          -121509406, 1048526839))
      expect(lg(1688647147, -1327540094) | lg(1767049400, -1609892586))
        .toEqual(lg(1844952571, -1327497834))
      expect(lg(-207425559, 231270892) | lg(515004644, -1349918716))
        .toEqual(lg(-5046291, -1345721876))
      expect(lg(781813534, 1274454635) | lg(-1814682890, -1182466103))
        .toEqual(lg(-1075861506, -67698709))
      expect(lg(1792206347, -54265949) | lg(931436592, -625499620)).toEqual(lg(
          2144796219, -17303617))
      expect(lg(-1957992337, 421859924) | lg(1138122674, -1896513908))
        .toEqual(lg(-874545153, -1611301156))
      expect(lg(-1220262128, 1790926509) | lg(-2107837994, -245286664))
        .toEqual(lg(-1218644010, -67141891))
      expect(lg(-485426246, 2122993116) | lg(-1077361187, 795578180))
        .toEqual(lg(-2555905, 2146160604))
      expect(lg(713580935, 2059541733) | lg(957494730, 1688940106)).toEqual(lg(
          999978447, 2129346287))
      expect(lg(-181332639, 608154803) | lg(787627150, 1378378253)).toEqual(lg(
          -836113, 1983903423))
      expect(lg(-944448827, -1528432780) | lg(-374967708, 364320051))
        .toEqual(lg(-273220891, -1242040457))
      expect(lg(1822361801, -1626992863) | lg(-1865553026, -1867721804))
        .toEqual(lg(-52433921, -1615929419))
      expect(lg(-333036705, -39743141) | lg(-136127263, -404241201))
        .toEqual(lg(-1646593, -1583649))
      expect(lg(1342309595, 143297662) | lg(-1448137844, -50933699))
        .toEqual(lg(-105959457, -50406273))
      expect(lg(-514802766, 718197230) | lg(1113082335, -259890518))
        .toEqual(lg(-480707585, -87100434))
      expect(lg(-476348284, -1025699402) | lg(1518405435, 1545110880))
        .toEqual(lg(-73693249, -555903498))
      expect(lg(-2058311589, 1135057747) | lg(-1664731675, -1535754941))
        .toEqual(lg(-1646871041, -403194029))
      expect(lg(333362997, -34482226) | lg(-205173969, 1754490115)).toEqual(lg(
          -203423937, -34342961))
      expect(lg(2083354303, -2043490039) | lg(1344953817, -195725679))
        .toEqual(lg(2083487743, -159909991))
      expect(lg(-942983837, -683124136) | lg(909452980, -1021249590))
        .toEqual(lg(-134268937, -680984614))
      expect(lg(-402624124, -505696678) | lg(-688199800, 2110291577))
        .toEqual(lg(-17107060, -35914117))
    }

    it("bitwise and &") {
      expect(lg(-1973652216, 353474049) & lg(-576365513, -1546420349))
        .toEqual(lg(-2012982272, 17896961))
      expect(lg(2054268182, -735220496) & lg(-1706223071, -653894309))
        .toEqual(lg(440467456, -805024688))
      expect(lg(-761230816, -1888512251) & lg(-988806710, -256349768))
        .toEqual(lg(-1073741824, -2144861952))
      expect(lg(-834874333, -101893315) & lg(-1964333382, -1877225849))
        .toEqual(lg(-1977056222, -1878455803))
      expect(lg(-767041747, 1403541430) & lg(-320482908, 442929698))
        .toEqual(lg(-1069166300, 304091682))
      expect(lg(1538292767, 67928849) & lg(261587146, 2097883842)).toEqual(lg(
          193986570, 67633664))
      expect(lg(448790964, 1852174074) & lg(-284620129, 35804464)).toEqual(lg(
          167772308, 35669040))
      expect(lg(123267521, 1965916169) & lg(-401979731, 588194498)).toEqual(lg(
          540801, 554500096))
      expect(lg(-1725202754, 324931273) & lg(-1240211271, 948007557))
        .toEqual(lg(-1878826824, 268436097))
      expect(lg(1044995460, -1447811559) & lg(1381579300, 378161591))
        .toEqual(lg(306780164, 8388625))
      expect(lg(1640550232, -1980050765) & lg(-1613988461, 381206391))
        .toEqual(lg(29904144, 12096051))
      expect(lg(-810205145, 832395272) & lg(-153237294, -1368559681))
        .toEqual(lg(-963297278, 537741320))
      expect(lg(-2087037677, -539042218) & lg(-1930915595, -1879201391))
        .toEqual(lg(-2138566639, -1881372656))
      expect(lg(936077102, 1888906741) & lg(-590306112, 153013360)).toEqual(lg(
          348136448, 1461360))
      expect(lg(-1520343420, -480326676) & lg(-1031638712, 463833361))
        .toEqual(lg(-2147459072, 50628864))
      expect(lg(-603625122, -837874740) & lg(-266310439, -433325742))
        .toEqual(lg(-805279656, -972355264))
      expect(lg(2101242821, 1363798717) & lg(-337523686, -1007893653))
        .toEqual(lg(1763723264, 1095287337))
      expect(lg(-849542331, 2084521938) & lg(1866786159, -179258269))
        .toEqual(lg(1296302405, 1947206722))
      expect(lg(1283984114, 1922846117) & lg(-42342754, 948944324)).toEqual(lg(
          1275593362, 814484868))
      expect(lg(18451376, 39592223) & lg(-300891980, 43819665)).toEqual(lg(
          1081520, 35397649))
      expect(lg(1772840110, -1611388521) & lg(876572201, -1080057992))
        .toEqual(lg(539714600, -1617688304))
      expect(lg(-1792575438, 1131693597) & lg(2026108738, -691967420))
        .toEqual(lg(268660738, 1111507460))
      expect(lg(-1977130853, 1105495064) & lg(-289941322, 37545108))
        .toEqual(lg(-1977139054, 2393104))
      expect(lg(-1590955612, -1330697458) & lg(-924798828, -1177272879))
        .toEqual(lg(-2145341308, -1333516032))
      expect(lg(-285931035, -293654078) & lg(-1486596765, -31342500))
        .toEqual(lg(-1503395487, -299827136))
      expect(lg(1237743775, -1293389691) & lg(1803860874, 1175174664))
        .toEqual(lg(1233401994, 34091008))
      expect(lg(-839976008, 900736195) & lg(-362132238, -668577850))
        .toEqual(lg(-932558672, 270533826))
      expect(lg(1202887172, 484547048) & lg(793351913, -1622877017))
        .toEqual(lg(117477888, 473995424))
      expect(lg(1393155525, -2025583778) & lg(-1164217783, -416769026))
        .toEqual(lg(302600257, -2030040226))
      expect(lg(-658787467, -1534848013) & lg(770509273, 861439716))
        .toEqual(lg(145293649, 536871648))
      expect(lg(1550840002, 1588870758) & lg(2084528882, 302148833))
        .toEqual(lg(1546608834, 302001248))
      expect(lg(481609689, -152204489) & lg(1279544421, -561242137))
        .toEqual(lg(201606209, -695465177))
      expect(lg(-1529763097, -1978531900) & lg(641783708, -2039026814))
        .toEqual(lg(608207492, -2112820352))
      expect(lg(887514076, -129985897) & lg(423346174, -1364800691))
        .toEqual(lg(270672860, -1476361723))
      expect(lg(1736270961, -223672071) & lg(748709016, -498985816))
        .toEqual(lg(606102544, -503185240))
      expect(lg(413438572, 628333003) & lg(-1964689415, -2039117914))
        .toEqual(lg(144970344, 74547586))
      expect(lg(-1441786846, -952014445) & lg(1364118108, 582220621))
        .toEqual(lg(0, 33646849))
      expect(lg(-167845571, -610782244) & lg(920048140, -1832380167))
        .toEqual(lg(886489100, -1836576552))
      expect(lg(1070668735, 1223734716) & lg(1255200260, 310500128))
        .toEqual(lg(181408260, 8425760))
      expect(lg(565998918, 2102701486) & lg(1230790357, -651115716))
        .toEqual(lg(18633796, 1494253868))
      expect(lg(1259021457, -988117846) & lg(-95497780, 2025257730))
        .toEqual(lg(1242169472, 1074954242))
      expect(lg(236334914, 210367602) & lg(-1388488109, 672191707)).toEqual(lg(
          202639938, 134272082))
      expect(lg(2029259749, 2012495659) & lg(-1125022313, -17866867))
        .toEqual(lg(955253125, 1994661641))
      expect(lg(2078335024, -748696528) & lg(-1944488853, 1455161657))
        .toEqual(lg(134242336, 1377566768))
      expect(lg(1962270604, 747650594) & lg(1051641707, -1606005365))
        .toEqual(lg(883214088, 536873986))
      expect(lg(504991188, 623990339) & lg(-1919047324, 331123498)).toEqual(lg(
          203000132, 19923458))
      expect(lg(409659995, 1887189252) & lg(384277491, 1973591160)).toEqual(lg(
          274893395, 1881151488))
      expect(lg(872793907, 353626075) & lg(34859627, 1988247415)).toEqual(lg(
          115235, 335685459))
      expect(lg(-1407266644, 441386073) & lg(1635378940, -548742904))
        .toEqual(lg(538493100, 441057288))
      expect(lg(844761371, 1022505085) & lg(1930384912, 688275291)).toEqual(lg(
          839516176, 671232089))
    }

    it("bitwise xor ^") {
      expect(lg(690404572, -1279287229) ^ lg(2032643064, 1592473506))
        .toEqual(lg(1342248740, -313223199))
      expect(lg(1880634009, 1433776255) ^ lg(-348716857, 1160616710))
        .toEqual(lg(-1691405730, 274213753))
      expect(lg(-958227509, 287282926) ^ lg(-227156354, -260614433))
        .toEqual(lg(882329013, -513228751))
      expect(lg(-266860160, 1815641996) ^ lg(-1536078487, -252396512))
        .toEqual(lg(1416185065, -1664302164))
      expect(lg(1278830943, 22194981) ^ lg(-127614265, 1402065425)).toEqual(lg(
          -1268929640, 1388542260))
      expect(lg(-865349911, -84319450) ^ lg(-1309551184, 1538105408))
        .toEqual(lg(2107251545, -1588280474))
      expect(lg(-1973252863, -1969367363) ^ lg(916708915, -2107399239))
        .toEqual(lg(-1128180942, 150893828))
      expect(lg(2098389933, -3394272) ^ lg(-1444158786, -35986574)).toEqual(lg(
          -721878765, 35051090))
      expect(lg(533612062, -1712875225) ^ lg(-1893500990, -2045945845))
        .toEqual(lg(-1863503396, 535478572))
      expect(lg(799833325, 2072025633) ^ lg(1223390615, -462316872))
        .toEqual(lg(1732708730, -1611595623))
      expect(lg(570370215, 1665373667) ^ lg(-215635812, -199487627))
        .toEqual(lg(-757432261, -1755342186))
      expect(lg(-1440978805, 1756956707) ^ lg(-2028544094, 1603010180))
        .toEqual(lg(755676969, 926086823))
      expect(lg(-1788434031, -203193594) ^ lg(-634323830, -1548988140))
        .toEqual(lg(1331057947, 1347408402))
      expect(lg(-1101798994, 1399594232) ^ lg(-1646597332, -1546197695))
        .toEqual(lg(596183682, -256181831))
      expect(lg(-1432962218, -1631098948) ^ lg(-75062662, -1809535684))
        .toEqual(lg(1360009516, 182700672))
      expect(lg(699430210, 902448324) ^ lg(180589540, -851178037)).toEqual(lg(
          594798246, -124892913))
      expect(lg(-1873118605, -20501824) ^ lg(553528574, -1833816077))
        .toEqual(lg(-1331407219, 1819608371))
      expect(lg(-693963147, 616673404) ^ lg(-1300017312, 952842738))
        .toEqual(lg(1679931669, 470452622))
      expect(lg(1250421224, 1104113895) ^ lg(610853582, -420437133))
        .toEqual(lg(1861159718, -1488989292))
      expect(lg(-611286212, -1550148499) ^ lg(-445979241, 1514412284))
        .toEqual(lg(1056597675, -102857583))
      expect(lg(1704771515, 1382796179) ^ lg(1792974657, 845718187))
        .toEqual(lg(255992058, 1610836280))
      expect(lg(1042258124, 728098489) ^ lg(752081254, 178455073)).toEqual(lg(
          315376042, 566682776))
      expect(lg(-1887944331, 1039677246) ^ lg(2073445080, -1177715779))
        .toEqual(lg(-185728083, -2076881789))
      expect(lg(1986213921, -1875380784) ^ lg(2000642315, -903708915))
        .toEqual(lg(22829354, 1511361245))
      expect(lg(365179043, -418125319) ^ lg(-1574194252, -2111511936))
        .toEqual(lg(-1209040105, 1698106233))
      expect(lg(-376038790, 1936322298) ^ lg(1865150125, -1725716895))
        .toEqual(lg(-2034371369, -364230501))
      expect(lg(182372182, -1389399582) ^ lg(-428511717, 121795327))
        .toEqual(lg(-324294323, -1435696355))
      expect(lg(408417754, -547668779) ^ lg(-2031925038, -640727503))
        .toEqual(lg(-1632322296, 110394084))
      expect(lg(1515701412, 860890032) ^ lg(105620727, -733936646)).toEqual(lg(
          1545363539, -418308022))
      expect(lg(144626057, 2121098703) ^ lg(-1983696154, 599907975))
        .toEqual(lg(-2124553361, 1571601224))
      expect(lg(1716685092, -647833300) ^ lg(-2015169962, -236730016))
        .toEqual(lg(-508527758, 679546956))
      expect(lg(-2016515438, -1674300757) ^ lg(1371710907, 306998239))
        .toEqual(lg(-703803607, -1904715404))
      expect(lg(-547404938, -860356684) ^ lg(1838979051, -234273060))
        .toEqual(lg(-1295788899, 1052686696))
      expect(lg(1034397763, 1158948099) ^ lg(-1774872572, -585891415))
        .toEqual(lg(-1416482745, -1744821078))
      expect(lg(1755131065, -847055172) ^ lg(-1905373301, 1520046660))
        .toEqual(lg(-420256974, -1759976200))
      expect(lg(755114159, 1707687361) ^ lg(-1492035880, -98945846))
        .toEqual(lg(-1978435977, -1613559541))
      expect(lg(1110955283, -394871226) ^ lg(407088050, 1436378667))
        .toEqual(lg(1517584033, -1108617107))
      expect(lg(729918767, -1047522396) ^ lg(1311993397, 527980061))
        .toEqual(lg(1706214170, -555203143))
      expect(lg(-1533968339, 1826223468) ^ lg(1274742780, -681737135))
        .toEqual(lg(-278231087, -1148948163))
      expect(lg(230297309, -219465279) ^ lg(-26402437, -1168671510))
        .toEqual(lg(-204001370, 1220298027))
      expect(lg(-1364422220, 1487677662) ^ lg(350226860, -557455315))
        .toEqual(lg(-1169385448, -2039889677))
      expect(lg(-1049451753, 1883174397) ^ lg(-296389651, 1475305844))
        .toEqual(lg(791138554, 668046473))
      expect(lg(-895088167, 1303802204) ^ lg(-1211781080, 258296169))
        .toEqual(lg(2103687665, 1121138741))
      expect(lg(1409034242, -1162000487) ^ lg(-1155284684, -1936324751))
        .toEqual(lg(-387978954, 908804328))
      expect(lg(861082066, -475962819) ^ lg(2015491450, -1480757658))
        .toEqual(lg(1265820840, 1142688859))
      expect(lg(-288714491, 1935424926) ^ lg(-1240144421, -1674954616))
        .toEqual(lg(1490973918, -277478122))
      expect(lg(-699164585, -731232280) ^ lg(-1144193327, -1043673420))
        .toEqual(lg(1839163014, 362842460))
      expect(lg(683993930, 248552821) ^ lg(220002260, -2040344874)).toEqual(lg(
          634920094, -2001579101))
      expect(lg(640417317, 298956382) ^ lg(-398074626, -554826341)).toEqual(lg(
          -831642917, -817908795))
      expect(lg(-1493347776, 1187436882) ^ lg(-1779986703, 550293355))
        .toEqual(lg(857398449, 1711937081))
    }

    it("shift left <<") {
      expect(lg(-1875389825, 1268606893) << - 73329513).toEqual(lg(1065353216,
                                                                   -691528727))
      expect(lg(869553861, -291578632) << - 339545061).toEqual(lg(671088640,
                                                                  -1046568266))
      expect(lg(543726956, -1753066291) << - 809014658).toEqual(lg(0, 0))
      expect(lg(-895322669, 847749031) << 1030973528).toEqual(lg(-754974720,
                                                                 -1479892363))
      expect(lg(1598039634, 819660072) << 82069876).toEqual(lg(0, 1696595968))
      expect(lg(-151740279, -595601314) << 503039850).toEqual(lg(0,
                                                                 -763223040))
      expect(lg(-1702267427, 1115684531) << 1171866675).toEqual(lg(
          0, -1360527360))
      expect(lg(-807341493, 286689824) << - 1938771891).toEqual(lg(508125184,
                                                                   -784066052))
      expect(lg(-382832750, -2134078182) << 1537970769).toEqual(lg(-551288832,
                                                                   439734876))
      expect(lg(-580904341, 939559401) << 1856717061).toEqual(lg(-1409069728,
                                                                 1129787))
      expect(lg(-198125160, 663832884) << 1561097110).toEqual(lg(1711276032,
                                                                 1295846454))
      expect(lg(-1199332365, -1728151952) << 858801923).toEqual(lg(-1004724328,
                                                                   -940313723))
      expect(lg(773140802, -181814355) << 1110910853).toEqual(lg(-1029298112,
                                                                 -1523092059))
      expect(lg(1601160689, 869229832) << - 338843811).toEqual(lg(536870912,
                                                                  200145086))
      expect(lg(-1919381932, -201750119) << - 813015128).toEqual(lg(
          0, -1735502848))
      expect(lg(-52019067, -102802849) << - 2122946486).toEqual(lg(-1727917056,
                                                                   2104066035))
      expect(lg(-456947922, 1170727731) << 2126487160).toEqual(lg(0,
                                                                  771751936))
      expect(lg(1756719200, -1702547414) << - 32425558).toEqual(lg(0,
                                                                   -710836224))
      expect(lg(97072750, 409070577) << 1222452733).toEqual(lg(0, -1073741824))
      expect(lg(1177105779, 212324545) << - 834196361).toEqual(lg(0,
                                                                  -1182793728))
      expect(lg(1395605166, -1743726419) << - 1762017159).toEqual(lg(
          0, 1543503872))
      expect(lg(703808254, 1939941481) << 1042647417).toEqual(lg(0, -67108864))
      expect(lg(-702184622, -618243162) << - 753853766).toEqual(lg(0,
                                                                   1207959552))
      expect(lg(-1368457662, 1747275710) << 1382741393).toEqual(lg(
          -58458112, -1619174179))
      expect(lg(-74885703, 1342895995) << 1929734882).toEqual(lg(0,
                                                                 -299542812))
      expect(lg(-61401466, -496528012) << - 129147274).toEqual(lg(0,
                                                                  -1585446912))
      expect(lg(-660169692, 1479330149) << 289081298).toEqual(lg(1888485376,
                                                                 630678170))
      expect(lg(-421237721, 1011668330) << 370873533).toEqual(lg(0,
                                                                 -536870912))
      expect(lg(-821818323, -2029348763) << - 916638609).toEqual(lg(0,
                                                                    102137856))
      expect(lg(-1246065172, -1572087360) << 1493241980).toEqual(lg(
          0, -1073741824))
      expect(lg(578258094, -906212820) << 2074806145).toEqual(lg(1156516188,
                                                                 -1812425640))
      expect(lg(61151968, -1770168701) << - 2062208020).toEqual(lg(0,
                                                                   1370357760))
      expect(lg(1013576541, 460756940) << - 902835237).toEqual(lg(-402653184,
                                                                  1642287002))
      expect(lg(-1731171245, 771836652) << 868975579).toEqual(lg(-1744830464,
                                                                 1690731362))
      expect(lg(1123258511, 1049676716) << 575477257).toEqual(lg(-417260032,
                                                                 563566725))
      expect(lg(-779579692, 1222433667) << 1238257604).toEqual(lg(411626816,
                                                                  -1915897795))
      expect(lg(-1102469156, -543766743) << 553354173).toEqual(lg(0,
                                                                  -2147483648))
      expect(lg(843520587, -517185932) << 1899246569).toEqual(lg(0,
                                                                 -1909156352))
      expect(lg(-510775647, -896837143) << 1487779500).toEqual(lg(0,
                                                                  -487976960))
      expect(lg(-1594115986, -186853391) << - 119255604).toEqual(lg(
          -1148788736, -847308273))
      expect(lg(-588635767, 1047291343) << 2089738146).toEqual(lg(0,
                                                                  1940424228))
      expect(lg(-1002017201, -986188138) << 800913356).toEqual(lg(1726279680,
                                                                  2137615428))
      expect(lg(1813551275, -400674286) << - 1609938966).toEqual(lg(
          0, 1650633728))
      expect(lg(-1333929801, 254558182) << - 1518372133).toEqual(lg(
          -1207959552, 897838789))
      expect(lg(834127324, 878312672) << - 923142549).toEqual(lg(0,
                                                                 -1104224256))
      expect(lg(126340223, -2008491127) << - 252023418).toEqual(lg(-504160320,
                                                                   305586753))
      expect(lg(510931784, -1313923431) << 1174528765).toEqual(lg(0, 0))
      expect(lg(711394099, -400560166) << - 967606846).toEqual(lg(-1449390900,
                                                                  -1602240664))
      expect(lg(1319282800, -1994311032) << 1237159401).toEqual(lg(0,
                                                                   1162928128))
      expect(lg(-874710629, -1242845989) << 484063041).toEqual(lg(-1749421258,
                                                                  1809275319))
    }

    it("shift logical right >>>") {
      expect(lg(88517143, 1273092247) >>> 2099569298).toEqual(lg(1982185809,
                                                                 4856))
      expect(lg(-1987462914, 1361836721) >>> - 2053535175).toEqual(lg(40, 0))
      expect(lg(1513792977, 1085974656) >>> - 303705162).toEqual(lg(258, 0))
      expect(lg(-2071249600, 1411897130) >>> 1015183069).toEqual(lg(
          -1589724844, 2))
      expect(lg(-1560865755, 214919778) >>> 1191603401).toEqual(lg(827423626,
                                                                   419765))
      expect(lg(944265510, -995896821) >>> 485744647).toEqual(lg(376475826,
                                                                 25773988))
      expect(lg(1131824263, -2080089658) >>> - 386336938).toEqual(lg(291969293,
                                                                     528))
      expect(lg(-827478170, -1185129975) >>> 2048537528).toEqual(lg(185, 0))
      expect(lg(-916869993, -1344352401) >>> - 791372688).toEqual(lg(45022, 0))
      expect(lg(588931659, -1830830904) >>> - 1259543946).toEqual(lg(587, 0))
      expect(lg(473794659, 947514265) >>> - 1409717873).toEqual(lg(-684574597,
                                                                   28915))
      expect(lg(471518489, -940479957) >>> - 847604034).toEqual(lg(3, 0))
      expect(lg(-818287716, 1547586919) >>> - 216455813).toEqual(lg(11, 0))
      expect(lg(-2088976011, -2057680935) >>> 787633143).toEqual(lg(266, 0))
      expect(lg(306848777, -497453644) >>> 1584315654).toEqual(lg(-800511856,
                                                                  59336150))
      expect(lg(-1689341833, -927188015) >>> 1300572337).toEqual(lg(25694, 0))
      expect(lg(396954515, 413418119) >>> 1180537031).toEqual(lg(237982231,
                                                                 3229829))
      expect(lg(1478732342, 1335401807) >>> - 1668840943).toEqual(lg(
          1319611409, 10188))
      expect(lg(-1326271298, -1643756084) >>> - 2118687716).toEqual(lg(
          -530293557, 9))
      expect(lg(1205635051, 875594107) >>> 350453433).toEqual(lg(26, 0))
      expect(lg(-2049358216, -553556680) >>> - 1203541232).toEqual(lg(
          1698203097, 57089))
      expect(lg(1278981121, -1661145698) >>> 254766480).toEqual(lg(-308392901,
                                                                   40188))
      expect(lg(1313272948, 929268302) >>> 1175504903).toEqual(lg(-1667461656,
                                                                  7259908))
      expect(lg(1982277801, -1050318135) >>> 629735727).toEqual(lg(99018, 0))
      expect(lg(-610510955, 1064153335) >>> 577897264).toEqual(lg(16237, 0))
      expect(lg(1859860682, 1413109554) >>> 243415787).toEqual(lg(689994, 0))
      expect(lg(1757351444, -7991214) >>> - 1844808396).toEqual(lg(4088, 0))
      expect(lg(-1277568919, -1194709070) >>> - 2102413146).toEqual(lg(
          48441534, 0))
      expect(lg(-1768551066, 1342559) >>> 365466523).toEqual(lg(42961906, 0))
      expect(lg(1051996382, -213518283) >>> - 717261067).toEqual(lg(1946, 0))
      expect(lg(451444747, -1380034334) >>> - 675522340).toEqual(lg(-605712863,
                                                                    10))
      expect(lg(605006440, -1956088854) >>> 192236860).toEqual(lg(8, 0))
      expect(lg(-384174131, -2122615661) >>> - 1278414057).toEqual(lg(
          -152492078, 258))
      expect(lg(-1579022332, -1953425763) >>> 2134440904).toEqual(lg(
          -1650335224, 9146646))
      expect(lg(-433112808, -1479030417) >>> - 1873327132).toEqual(lg(
          175996054, 0))
      expect(lg(-1786180708, 385945228) >>> 1526047775).toEqual(lg(771890457,
                                                                   0))
      expect(lg(868056695, -1200391723) >>> 93595840).toEqual(lg(868056695,
                                                                 -1200391723))
      expect(lg(1335240662, -1403745666) >>> 1625850351).toEqual(lg(88233, 0))
      expect(lg(-681452715, -1446696044) >>> - 742234373).toEqual(lg(21, 0))
      expect(lg(301750839, 1600782865) >>> 1678034787).toEqual(lg(200097858,
                                                                  0))
      expect(lg(-2077889650, 445749598) >>> 363036476).toEqual(lg(1, 0))
      expect(lg(-1633078438, 1644025478) >>> - 1297864237).toEqual(lg(
          -1160719403, 3135))
      expect(lg(1159483779, 906375175) >>> - 1204888593).toEqual(lg(27660, 0))
      expect(lg(179807326, 1050325098) >>> - 1598422013).toEqual(lg(1096217739,
                                                                    131290637))
      expect(lg(952383136, -193355640) >>> 415626042).toEqual(lg(61, 0))
      expect(lg(972435428, -1130194211) >>> - 1259042456).toEqual(lg(12362394,
                                                                     0))
      expect(lg(605823642, 555993310) >>> 1780437072).toEqual(lg(-924965860,
                                                                 8483))
      expect(lg(665774635, 184915839) >>> 1729784373).toEqual(lg(88, 0))
      expect(lg(-263808048, -741669613) >>> - 204793551).toEqual(lg(27109, 0))
      expect(lg(-954198224, 369053217) >>> 768150041).toEqual(lg(-5828381, 10))
    }

    it("shift arithmetic right >>") {
      expect(lg(-1780076655, 720252680) >> - 1316031160).toEqual(lg(144041519,
                                                                    2813487))
      expect(lg(234061537, 796729805) >> 1452874739).toEqual(lg(1519, 0))
      expect(lg(1523206972, 1046748891) >> 1356453463).toEqual(lg(-935479627,
                                                                  124))
      expect(lg(1866043067, -2009962307) >> 393061105).toEqual(lg(-15335, -1))
      expect(lg(89507691, 183545611) >> - 1980770119).toEqual(lg(5, 0))
      expect(lg(-1062312593, 1831556953) >> 1545082311).toEqual(lg(-1283367734,
                                                                   14309038))
      expect(lg(-1568293714, 523169438) >> - 2119005984).toEqual(lg(523169438,
                                                                    0))
      expect(lg(-2013675422, -748851607) >> 511130378).toEqual(lg(-1704853904,
                                                                  -731301))
      expect(lg(-521585277, -770402055) >> - 1176556648).toEqual(lg(345569760,
                                                                    -46))
      expect(lg(-145701849, 257587932) >> - 1512809002).toEqual(lg(1777038301,
                                                                   61))
      expect(lg(-973180026, -1694110170) >> 2083093369).toEqual(lg(-51, -1))
      expect(lg(1761120319, -539393529) >> - 207994821).toEqual(lg(-5, -1))
      expect(lg(-30904807, -1662128199) >> - 638486135).toEqual(lg(-587262921,
                                                                   -3246345))
      expect(lg(1812122560, -701571284) >> 611632432).toEqual(lg(-10706, -1))
      expect(lg(119750375, 1605805472) >> 244039684).toEqual(lg(7484398,
                                                                100362842))
      expect(lg(269986751, 1459449758) >> - 439796226).toEqual(lg(1, 0))
      expect(lg(-1969890020, 2011804532) >> - 652735044).toEqual(lg(7, 0))
      expect(lg(-1582649974, 826310885) >> 613066583).toEqual(lg(-2130588861,
                                                                 98))
      expect(lg(756433442, -1459944907) >> - 775565931).toEqual(lg(-669931160,
                                                                   -697))
      expect(lg(1678061064, -1680910162) >> - 531660641).toEqual(lg(933146972,
                                                                    -1))
      expect(lg(1870355258, 332325727) >> - 434372344).toEqual(lg(1601141595,
                                                                  1298147))
      expect(lg(1886551280, -2122502046) >> - 763866098).toEqual(lg(
          -1047936567, -129548))
      expect(lg(-1169141408, -592336405) >> - 1841005139).toEqual(lg(-72307,
                                                                     -1))
      expect(lg(686282122, 295988927) >> 69079212).toEqual(lg(72262, 0))
      expect(lg(1825529126, -1527213400) >> 1371712838).toEqual(lg(-1582088844,
                                                                   -23862710))
      expect(lg(633149491, 1126324183) >> 1948323684).toEqual(lg(70395261, 0))
      expect(lg(-363762029, -1377253181) >> - 1243200330).toEqual(lg(-329, -1))
      expect(lg(-1694234908, -689608667) >> 728732313).toEqual(lg(1924403917,
                                                                  -21))
      expect(lg(1319661865, -2053067582) >> - 777879057).toEqual(lg(-62655,
                                                                    -1))
      expect(lg(-1472236443, 19900875) >> 373478400).toEqual(lg(-1472236443,
                                                                19900875))
      expect(lg(-1719111010, -1766452468) >> 942391743).toEqual(lg(-1, -1))
      expect(lg(-624682758, 1345231635) >> - 813574478).toEqual(lg(5131, 0))
      expect(lg(1316519660, 314590421) >> - 641829383).toEqual(lg(9, 0))
      expect(lg(-1380652891, -474856510) >> - 920501329).toEqual(lg(-14492,
                                                                    -1))
      expect(lg(-2084688189, 1352268039) >> - 177471111).toEqual(lg(40, 0))
      expect(lg(507881044, 1779362534) >> - 508943033).toEqual(lg(-868447412,
                                                                  13901269))
      expect(lg(1742323077, -1229747072) >> 401183471).toEqual(lg(-37529, -1))
      expect(lg(346182810, 770838817) >> 797274667).toEqual(lg(376386, 0))
      expect(lg(828281422, -477411393) >> 1298272370).toEqual(lg(-1822, -1))
      expect(lg(-341778503, -671026265) >> 532386578).toEqual(lg(1021967080,
                                                                 -2560))
      expect(lg(-1907127360, 1144311248) >> - 2131012273).toEqual(lg(
          -1683940185, 34921))
      expect(lg(756366897, -1994294687) >> - 1642432978).toEqual(lg(-121723,
                                                                    -1))
      expect(lg(-1363894143, 1241756453) >> 1681307793).toEqual(lg(-644688038,
                                                                   9473))
      expect(lg(1708006412, -1138876437) >> 2010442220).toEqual(lg(-278047,
                                                                   -1))
      expect(lg(-664430929, 446891142) >> - 1707024855).toEqual(lg(872834, 0))
      expect(lg(-1904131429, -938887) >> - 829231944).toEqual(lg(-1, -1))
      expect(lg(-1043053889, 1572668786) >> 309495249).toEqual(lg(-2101780246,
                                                                  11998))
      expect(lg(563683687, -1497656119) >> - 176819791).toEqual(lg(-11427, -1))
      expect(lg(-627312011, 421917318) >> 2056663541).toEqual(lg(201, 0))
      expect(lg(-904956287, -543423347) >> - 617227620).toEqual(lg(-104838948,
                                                                   -3))
    }

    it("negate -") {
      expect(-lg(0)).toEqual(lg(0))
      expect(-lg(-1)).toEqual(lg(1))
      expect(-lg(1)).toEqual(lg(-1))
      expect(-MaxVal).toEqual(lg(1, -2147483648))
      expect(-MinVal).toEqual(MinVal)
      expect(-lg(0, 1)).toEqual(lg(0, -1))

      expect(-lg(-792771844, 1518464954)).toEqual(lg(792771844, -1518464955))
      expect(-lg(-1313283210, 1172119605)).toEqual(lg(1313283210, -1172119606))
      expect(-lg(1034897743, 341494685)).toEqual(lg(-1034897743, -341494686))
      expect(-lg(924881290, -1614058539)).toEqual(lg(-924881290, 1614058538))
      expect(-lg(1636891236, 1405401039)).toEqual(lg(-1636891236, -1405401040))
      expect(-lg(-2044349674, 477271432)).toEqual(lg(2044349674, -477271433))
      expect(-lg(-1426086684, 1493816435)).toEqual(lg(1426086684, -1493816436))
      expect(-lg(2125201680, -1667846200)).toEqual(lg(-2125201680, 1667846199))
      expect(-lg(-161054645, 1272528724)).toEqual(lg(161054645, -1272528725))
      expect(-lg(1013390126, 1323844682)).toEqual(lg(-1013390126, -1323844683))
      expect(-lg(1028806094, 691441880)).toEqual(lg(-1028806094, -691441881))
      expect(-lg(-1060422114, 11477648)).toEqual(lg(1060422114, -11477649))
      expect(-lg(-1366334123, 2046238760)).toEqual(lg(1366334123, -2046238761))
      expect(-lg(-1307711795, -940346050)).toEqual(lg(1307711795, 940346049))
      expect(-lg(-421687960, 250174761)).toEqual(lg(421687960, -250174762))
      expect(-lg(-379452754, 843386802)).toEqual(lg(379452754, -843386803))
      expect(-lg(1251296999, -1144268298)).toEqual(lg(-1251296999, 1144268297))
      expect(-lg(690359429, 1676679601)).toEqual(lg(-690359429, -1676679602))
      expect(-lg(-1952563749, 882544419)).toEqual(lg(1952563749, -882544420))
      expect(-lg(1420900897, 1865273590)).toEqual(lg(-1420900897, -1865273591))
      expect(-lg(-115947827, 832851216)).toEqual(lg(115947827, -832851217))
      expect(-lg(1834973959, 1423776004)).toEqual(lg(-1834973959, -1423776005))
      expect(-lg(-1376766876, -1519617585)).toEqual(lg(1376766876, 1519617584))
      expect(-lg(1845217535, -724725866)).toEqual(lg(-1845217535, 724725865))
      expect(-lg(1133294381, -699400554)).toEqual(lg(-1133294381, 699400553))
      expect(-lg(-113507585, -615978890)).toEqual(lg(113507585, 615978889))
      expect(-lg(1839784424, -1163726653)).toEqual(lg(-1839784424, 1163726652))
      expect(-lg(-1065777168, -1301742164)).toEqual(lg(1065777168, 1301742163))
      expect(-lg(-334075220, 1058529733)).toEqual(lg(334075220, -1058529734))
      expect(-lg(-1443112398, -1148167881)).toEqual(lg(1443112398, 1148167880))
      expect(-lg(-1647739462, -12310883)).toEqual(lg(1647739462, 12310882))
      expect(-lg(-1461318149, -518941732)).toEqual(lg(1461318149, 518941731))
      expect(-lg(-56833825, 162898591)).toEqual(lg(56833825, -162898592))
      expect(-lg(680096727, 1760413868)).toEqual(lg(-680096727, -1760413869))
      expect(-lg(-461541717, 1103626949)).toEqual(lg(461541717, -1103626950))
      expect(-lg(-1287248387, -1483137215)).toEqual(lg(1287248387, 1483137214))
      expect(-lg(1681467124, 1197977022)).toEqual(lg(-1681467124, -1197977023))
      expect(-lg(310946355, -885055748)).toEqual(lg(-310946355, 885055747))
      expect(-lg(717629012, 1299204707)).toEqual(lg(-717629012, -1299204708))
      expect(-lg(-800584851, -350245994)).toEqual(lg(800584851, 350245993))
      expect(-lg(-1911014238, 441020785)).toEqual(lg(1911014238, -441020786))
      expect(-lg(1647080824, 1197295588)).toEqual(lg(-1647080824, -1197295589))
      expect(-lg(925751968, 479541399)).toEqual(lg(-925751968, -479541400))
      expect(-lg(656919119, -1574890073)).toEqual(lg(-656919119, 1574890072))
      expect(-lg(1833364814, -432106463)).toEqual(lg(-1833364814, 432106462))
      expect(-lg(315730911, 1990201784)).toEqual(lg(-315730911, -1990201785))
      expect(-lg(-1218524771, 572482047)).toEqual(lg(1218524771, -572482048))
      expect(-lg(-276668811, -2002398730)).toEqual(lg(276668811, 2002398729))
      expect(-lg(-1489416833, -834462754)).toEqual(lg(1489416833, 834462753))
      expect(-lg(-2066446588, -688546121)).toEqual(lg(2066446588, 688546120))
    }

    it("plus +") {
      expect(lg(-202981355, -566689628) + lg(1005131087, 0)).toEqual(lg(
          802149732, -566689627))
      expect(lg(1153016325, 1674149440) + lg(-250247224, -1)).toEqual(lg(
          902769101, 1674149440))
      expect(lg(1701699755, -1965159800) + lg(-573053270, -1)).toEqual(lg(
          1128646485, -1965159800))
      expect(lg(-1183294843, -973893590) + lg(1250231259, 0)).toEqual(lg(
          66936416, -973893589))
      expect(lg(-2145882999, 449544496) + lg(1990064998, 0)).toEqual(lg(
          -155818001, 449544496))
      expect(lg(-528276750, -917980205) + lg(-716322894, -1)).toEqual(lg(
          -1244599644, -917980205))
      expect(lg(-1061043923, 1794016498) + lg(1641637933, 0)).toEqual(lg(
          580594010, 1794016499))
      expect(lg(-315483661, 1883156001) + lg(-1559068210, -1)).toEqual(lg(
          -1874551871, 1883156001))
      expect(lg(-1899047326, 95409025) + lg(1287459517, 0)).toEqual(lg(
          -611587809, 95409025))
      expect(lg(-705065818, 1167571449) + lg(-688682067, -1)).toEqual(lg(
          -1393747885, 1167571449))
      expect(lg(-192210545, -607437554) + lg(1327945299, 0)).toEqual(lg(
          1135734754, -607437553))
      expect(lg(11453726, -2007097641) + lg(534018444, 0)).toEqual(lg(
          545472170, -2007097641))
      expect(lg(1809973610, -1191350400) + lg(500964333, 0)).toEqual(lg(
          -1984029353, -1191350400))
      expect(lg(972641234, 108684756) + lg(58650386, 0)).toEqual(lg(1031291620,
                                                                    108684756))
      expect(lg(-1511325903, 127758048) + lg(135565137, 0)).toEqual(lg(
          -1375760766, 127758048))
      expect(lg(-942832491, 429508921) + lg(1583511963, 0)).toEqual(lg(
          640679472, 429508922))
      expect(lg(1500842230, -594798242) + lg(1973621483, 0)).toEqual(lg(
          -820503583, -594798242))
      expect(lg(-1088230684, 910473912) + lg(-1331434717, -1)).toEqual(lg(
          1875301895, 910473912))
      expect(lg(798219431, 378724963) + lg(1740882894, 0)).toEqual(lg(
          -1755864971, 378724963))
      expect(lg(-1763683665, -683558197) + lg(-2063230727, -1)).toEqual(lg(
          468052904, -683558197))
      expect(lg(627629519, -1636478024) + lg(-2116479866, -1)).toEqual(lg(
          -1488850347, -1636478025))
      expect(lg(-526665240, -338305026) + lg(1442547647, 0)).toEqual(lg(
          915882407, -338305025))
      expect(lg(-1265295286, -466473801) + lg(314413183, 0)).toEqual(lg(
          -950882103, -466473801))
      expect(lg(-1412852606, -1417005301) + lg(739574383, 0)).toEqual(lg(
          -673278223, -1417005301))
      expect(lg(708993121, -2035157269) + lg(2020674339, 0)).toEqual(lg(
          -1565299836, -2035157269))
      expect(lg(847269791, 1182702858) + lg(-208540595, -1)).toEqual(lg(
          638729196, 1182702858))
      expect(lg(97084677, -1902383954) + lg(-1550736122, -1)).toEqual(lg(
          -1453651445, -1902383955))
      expect(lg(-267181534, -606967005) + lg(1383751193, 0)).toEqual(lg(
          1116569659, -606967004))
      expect(lg(-904322265, 1063184819) + lg(1433370295, 0)).toEqual(lg(
          529048030, 1063184820))
      expect(lg(1841727454, 101142421) + lg(1953979618, 0)).toEqual(lg(
          -499260224, 101142421))
      expect(lg(-1716387490, 1045175929) + lg(-1125714932, -1)).toEqual(lg(
          1452864874, 1045175929))
      expect(lg(-1020814821, 1506316756) + lg(2003551542, 0)).toEqual(lg(
          982736721, 1506316757))
      expect(lg(467820886, 1107506956) + lg(-1945885691, -1)).toEqual(lg(
          -1478064805, 1107506955))
      expect(lg(-103701719, -57552833) + lg(1540648885, 0)).toEqual(lg(
          1436947166, -57552832))
      expect(lg(1280780483, -414981457) + lg(-1276893027, -1)).toEqual(lg(
          3887456, -414981457))
      expect(lg(-1505747919, 606376864) + lg(-1850135506, -1)).toEqual(lg(
          939083871, 606376864))
      expect(lg(-1797917239, -606274238) + lg(636421914, 0)).toEqual(lg(
          -1161495325, -606274238))
      expect(lg(-551974000, 52949338) + lg(-1596979514, -1)).toEqual(lg(
          2146013782, 52949338))
      expect(lg(484182807, -623553408) + lg(-643244860, -1)).toEqual(lg(
          -159062053, -623553409))
      expect(lg(1170065239, 371486519) + lg(510095074, 0)).toEqual(lg(
          1680160313, 371486519))
      expect(lg(553737773, -251530660) + lg(1669491974, 0)).toEqual(lg(
          -2071737549, -251530660))
      expect(lg(1363264202, -324566030) + lg(-569386551, -1)).toEqual(lg(
          793877651, -324566030))
      expect(lg(1461362302, 1255689015) + lg(436194663, 0)).toEqual(lg(
          1897556965, 1255689015))
      expect(lg(-1463314706, 718534179) + lg(922446648, 0)).toEqual(lg(
          -540868058, 718534179))
      expect(lg(-1684072850, -716998233) + lg(1686620381, 0)).toEqual(lg(
          2547531, -716998232))
      expect(lg(-183257712, -2086072551) + lg(-1526555559, -1)).toEqual(lg(
          -1709813271, -2086072551))
      expect(lg(-485818523, -1223154956) + lg(-1648523419, -1)).toEqual(lg(
          -2134341942, -1223154956))
      expect(lg(392330048, -1934382665) + lg(1242289638, 0)).toEqual(lg(
          1634619686, -1934382665))
      expect(lg(1907808353, -75135322) + lg(977231853, 0)).toEqual(lg(
          -1409927090, -75135322))
      expect(lg(88305723, 1362535803) + lg(-1481307045, -1)).toEqual(lg(
          -1393001322, 1362535802))
    }

    it("minus -") {
      // Workaround for https://code.google.com/p/v8/issues/detail?id=3304
      expect(lg(0) - lg(1)).toEqual(lg(-1))

      expect(lg(406229717, 462416044) - lg(-911848978, -1)).toEqual(lg(
          1318078695, 462416044))
      expect(lg(873646396, 466142261) - lg(414233982, 0)).toEqual(lg(
          459412414, 466142261))
      expect(lg(-2077914189, -573388520) - lg(467630401, 0)).toEqual(lg(
          1749422706, -573388520))
      expect(lg(-789253983, -1980988132) - lg(-1645120336, -1)).toEqual(lg(
          855866353, -1980988131))
      expect(lg(-482388232, 1825277273) - lg(1954093602, 0)).toEqual(lg(
          1858485462, 1825277273))
      expect(lg(-1616159373, -1077757379) - lg(1467199419, 0)).toEqual(lg(
          1211608504, -1077757379))
      expect(lg(-105778670, -1825579414) - lg(1285633111, 0)).toEqual(lg(
          -1391411781, -1825579414))
      expect(lg(1306759468, -2018677385) - lg(-267161569, -1)).toEqual(lg(
          1573921037, -2018677385))
      expect(lg(618139116, -289291128) - lg(-1457699858, -1)).toEqual(lg(
          2075838974, -289291128))
      expect(lg(1736445522, -1980710784) - lg(1136432395, 0)).toEqual(lg(
          600013127, -1980710784))
      expect(lg(-1970971750, 21136449) - lg(-1412537571, -1)).toEqual(lg(
          -558434179, 21136449))
      expect(lg(-1491842755, 229693364) - lg(-1148192639, -1)).toEqual(lg(
          -343650116, 229693364))
      expect(lg(2125082313, -2064363005) - lg(439010339, 0)).toEqual(lg(
          1686071974, -2064363005))
      expect(lg(922634658, -1887690341) - lg(-1785080227, -1)).toEqual(lg(
          -1587252411, -1887690341))
      expect(lg(478015362, 1754335329) - lg(1470432050, 0)).toEqual(lg(
          -992416688, 1754335328))
      expect(lg(-1788952896, -845578935) - lg(787746350, 0)).toEqual(lg(
          1718268050, -845578935))
      expect(lg(-1177368338, -1479013672) - lg(1801279447, 0)).toEqual(lg(
          1316319511, -1479013672))
      expect(lg(1761081661, -2147323821) - lg(192205100, 0)).toEqual(lg(
          1568876561, -2147323821))
      expect(lg(261772552, 1604940225) - lg(1384264283, 0)).toEqual(lg(
          -1122491731, 1604940224))
      expect(lg(-1441241840, 1018615990) - lg(1296729001, 0)).toEqual(lg(
          1556996455, 1018615990))
      expect(lg(907527568, -155632233) - lg(959786241, 0)).toEqual(lg(
          -52258673, -155632234))
      expect(lg(1509034771, 1534910973) - lg(-402776628, -1)).toEqual(lg(
          1911811399, 1534910973))
      expect(lg(-344668006, -718856465) - lg(-1579173309, -1)).toEqual(lg(
          1234505303, -718856464))
      expect(lg(-2096618226, 1792314521) - lg(934525319, 0)).toEqual(lg(
          1263823751, 1792314521))
      expect(lg(1861956484, -977488448) - lg(-531140528, -1)).toEqual(lg(
          -1901870284, -977488448))
      expect(lg(-691455907, -1532994270) - lg(-861516811, -1)).toEqual(lg(
          170060904, -1532994269))
      expect(lg(-693769914, -946809431) - lg(-276525192, -1)).toEqual(lg(
          -417244722, -946809431))
      expect(lg(-1698674051, -834216711) - lg(1203787429, 0)).toEqual(lg(
          1392505816, -834216711))
      expect(lg(1453492556, -930632047) - lg(1114387533, 0)).toEqual(lg(
          339105023, -930632047))
      expect(lg(-516102112, -422836103) - lg(-2104772210, -1)).toEqual(lg(
          1588670098, -422836102))
      expect(lg(1194707556, 1839759286) - lg(-1306927198, -1)).toEqual(lg(
          -1793332542, 1839759286))
      expect(lg(1188016800, -1652840750) - lg(-1173206901, -1)).toEqual(lg(
          -1933743595, -1652840750))
      expect(lg(-1268512415, 1790839027) - lg(1853779377, 0)).toEqual(lg(
          1172675504, 1790839027))
      expect(lg(-777434907, 275932678) - lg(1260810171, 0)).toEqual(lg(
          -2038245078, 275932678))
      expect(lg(607917442, 658575619) - lg(1248037638, 0)).toEqual(lg(
          -640120196, 658575618))
      expect(lg(-1490388970, -2089057829) - lg(-551184357, -1)).toEqual(lg(
          -939204613, -2089057829))
      expect(lg(-1342917439, 992436418) - lg(746979592, 0)).toEqual(lg(
          -2089897031, 992436418))
      expect(lg(-1045496394, -1192540532) - lg(-278449623, -1)).toEqual(lg(
          -767046771, -1192540532))
      expect(lg(1555450000, -683257085) - lg(820258106, 0)).toEqual(lg(
          735191894, -683257085))
      expect(lg(1022728181, 481753248) - lg(-1003692417, -1)).toEqual(lg(
          2026420598, 481753248))
      expect(lg(2028304312, 1411964223) - lg(-134013562, -1)).toEqual(lg(
          -2132649422, 1411964223))
      expect(lg(704117341, -217374406) - lg(-642306919, -1)).toEqual(lg(
          1346424260, -217374406))
      expect(lg(313351245, 278237511) - lg(1006229802, 0)).toEqual(lg(
          -692878557, 278237510))
      expect(lg(2076724262, 2054685372) - lg(-672962991, -1)).toEqual(lg(
          -1545280043, 2054685372))
      expect(lg(1990098163, 261806288) - lg(833446186, 0)).toEqual(lg(
          1156651977, 261806288))
      expect(lg(1425435353, 1626774418) - lg(1669982892, 0)).toEqual(lg(
          -244547539, 1626774417))
      expect(lg(2084724465, -1714068645) - lg(-2084385716, -1)).toEqual(lg(
          -125857115, -1714068645))
      expect(lg(-1799809279, -543675020) - lg(324617484, 0)).toEqual(lg(
          -2124426763, -543675020))
      expect(lg(1972622018, -602489858) - lg(-177176047, -1)).toEqual(lg(
          -2145169231, -602489858))
      expect(lg(883147297, 967789979) - lg(474187246, 0)).toEqual(lg(
          408960051, 967789979))
    }

    it("times *") {
      expect(lg(-1436299491, 1172705251) * lg(1721031968, 0)).toEqual(lg(
          -1056314208, 1039912134))
      expect(lg(-1754547158, 1592794750) * lg(-850659149, -1)).toEqual(lg(
          15417694, -1235494072))
      expect(lg(-582562130, 1508550574) * lg(-2054981347, -1)).toEqual(lg(
          -1312839754, -486483117))
      expect(lg(-517256163, 1107889737) * lg(324089381, 0)).toEqual(lg(
          -377676239, 1969822597))
      expect(lg(1862517504, -2146745095) * lg(2043533548, 0)).toEqual(lg(
          -1426078720, -1379092277))
      expect(lg(-1341087062, 93674761) * lg(1272468928, 0)).toEqual(lg(
          -1611894400, 514550890))
      expect(lg(-1911825604, 1026411170) * lg(244738503, 0)).toEqual(lg(
          88803236, -172420721))
      expect(lg(2102189793, 425022510) * lg(750432219, 0)).toEqual(lg(
          1486387579, 668666773))
      expect(lg(-1628887094, 2043879870) * lg(-1367964491, -1)).toEqual(lg(
          913918418, 2124658288))
      expect(lg(454909009, -1096315634) * lg(-461844145, -1)).toEqual(lg(
          -1067082241, 864193319))
      expect(lg(-1346593793, -331776468) * lg(1495188289, 0)).toEqual(lg(
          949541055, 403324299))
      expect(lg(39946028, -363039140) * lg(-1134101206, -1)).toEqual(lg(
          -232871624, -1943313306))
      expect(lg(769959254, -432157368) * lg(-488368768, -1)).toEqual(lg(
          -528828160, -1884969955))
      expect(lg(1975078475, 1181124823) * lg(-1852476533, -1)).toEqual(lg(
          913322937, -2105457977))
      expect(lg(-2118478876, -1521449422) * lg(-235907376, -1)).toEqual(lg(
          1594278208, 943829214))
      expect(lg(-192590815, -1552754278) * lg(990887112, 0)).toEqual(lg(
          -50678328, 2146883835))
      expect(lg(-74714605, 386143916) * lg(1634792395, 0)).toEqual(lg(
          1779498513, -1732099612))
      expect(lg(1839773441, -590412588) * lg(799604314, 0)).toEqual(lg(
          982209626, 857499597))
      expect(lg(1395571130, -1228992407) * lg(1440046952, 0)).toEqual(lg(
          1806268816, -990479821))
      expect(lg(-1094818235, 1759139279) * lg(-156634285, -1)).toEqual(lg(
          1683728223, -957382628))
      expect(lg(853844787, 525523561) * lg(600761926, 0)).toEqual(lg(
          -1590791694, 595489480))
      expect(lg(-903115469, 793487771) * lg(1986597957, 0)).toEqual(lg(
          1353714367, 146465211))
      expect(lg(-830036223, 830164681) * lg(-1711884663, -1)).toEqual(lg(
          1421874569, -1462441210))
      expect(lg(1514898873, 1802395563) * lg(1763957470, 0)).toEqual(lg(
          -962035602, -2086325336))
      expect(lg(-1931885288, 136587512) * lg(-241565738, -1)).toEqual(lg(
          213232144, -1084932179))
      expect(lg(571274323, 1264898114) * lg(1823828906, 0)).toEqual(lg(
          -915935202, 1495104097))
      expect(lg(-15708939, -2105030313) * lg(48280153, 0)).toEqual(lg(
          1116543789, -1473151538))
      expect(lg(1792017337, -1626094957) * lg(301685947, 0)).toEqual(lg(
          -1230228445, -570579388))
      expect(lg(-1942632452, -691115342) * lg(-889918259, -1)).toEqual(lg(
          1335719116, 1447187791))
      expect(lg(-683458011, -1409200935) * lg(-996910555, -1)).toEqual(lg(
          1398640985, -1330552693))
      expect(lg(562125786, -1303526635) * lg(-1761056509, -1)).toEqual(lg(
          -402621042, 1775759707))
      expect(lg(2115902292, -1194658096) * lg(-1549721205, -1)).toEqual(lg(
          129149596, -78429064))
      expect(lg(1852083423, 330104035) * lg(1414822755, 0)).toEqual(lg(
          1706925885, 1413499189))
      expect(lg(-1623207532, 1442771787) * lg(-948878276, -1)).toEqual(lg(
          -722178384, 1850552711))
      expect(lg(-898643831, 773279296) * lg(1294488911, 0)).toEqual(lg(
          545021767, -1389368834))
      expect(lg(421823854, 802578424) * lg(1394107269, 0)).toEqual(lg(
          1541594150, 820379725))
      expect(lg(1589092022, 237831212) * lg(-763790472, -1)).toEqual(lg(
          -279324848, 1175391379))
      expect(lg(-1247207721, -370556328) * lg(1449901386, 0)).toEqual(lg(
          2089067814, 975727054))
      expect(lg(109386811, 368962517) * lg(1406834819, 0)).toEqual(lg(
          -1977714127, -377823390))
      expect(lg(2135299059, -798752868) * lg(-1861488893, -1)).toEqual(lg(
          1759713497, -312922364))
      expect(lg(-695671854, 1917612060) * lg(2083344781, 0)).toEqual(lg(
          1030024362, -795941843))
      expect(lg(250669253, -442179349) * lg(-552836178, -1)).toEqual(lg(
          -704748314, 388197332))
      expect(lg(1237744278, 206295616) * lg(-1547545223, -1)).toEqual(lg(
          758103782, -158300478))
      expect(lg(492775518, 1691641907) * lg(1172634963, 0)).toEqual(lg(
          -629736326, 810097466))
      expect(lg(-1549380722, 49835026) * lg(-1645815552, -1)).toEqual(lg(
          610754048, 1997636055))
      expect(lg(1850430325, -1942955614) * lg(-295254732, -1)).toEqual(lg(
          1696857284, 1549588995))
      expect(lg(-1276671498, -1984743584) * lg(-1583554303, -1)).toEqual(lg(
          -66011146, -376837532))
      expect(lg(-2127158934, -2058421178) * lg(1620104636, 0)).toEqual(lg(
          2033040344, -167450557))
      expect(lg(69958717, -772556465) * lg(21655944, 0)).toEqual(lg(
          -1886196376, -31345953))
      expect(lg(406538265, -107036516) * lg(2077087683, 0)).toEqual(lg(
          -38147573, -1269583268))
    }

    it("divide /") {
      expectThrows[ArithmeticException](lg(0) / lg(0))
      expectThrows[ArithmeticException](lg(5, 0) / lg(0))
      expectThrows[ArithmeticException](lg(0, 5) / lg(0))
      expectThrows[ArithmeticException](lg(-1) / lg(0))
      expectThrows[ArithmeticException](lg(-1, 0) / lg(0))

      expect(IntMinVal / lg(-1)).toEqual(IntMaxValPlus1)
      expect(IntMinVal / IntMaxValPlus1).toEqual(lg(-1))
      expect(IntMaxValPlus1 / lg(-1)).toEqual(IntMinVal)
      expect(IntMaxValPlus1 / IntMinVal).toEqual(lg(-1))

      expect(MaxVal / lg(-1)).toEqual(lg(1, -2147483648))
      expect(MinVal / lg(1)).toEqual(MinVal)
      expect(MinVal / lg(-1)).toEqual(MinVal)

      // int32 / int32
      expect(lg(-10426835, -1) / lg(-6243356, -1)).toEqual(lg(1, 0))
      expect(lg(49659080, 0) / lg(-170373, -1)).toEqual(lg(-291, -1))
      expect(lg(97420, 0) / lg(27521, 0)).toEqual(lg(3, 0))
      expect(lg(-9881291, -1) / lg(-366, -1)).toEqual(lg(26998, 0))
      expect(lg(-40, -1) / lg(81, 0)).toEqual(lg(0, 0))
      expect(lg(-6007, -1) / lg(-326806, -1)).toEqual(lg(0, 0))
      expect(lg(202, 0) / lg(-112, -1)).toEqual(lg(-1, -1))
      expect(lg(0, 0) / lg(47, 0)).toEqual(lg(0, 0))
      expect(lg(22667160, 0) / lg(70, 0)).toEqual(lg(323816, 0))
      expect(lg(254, 0) / lg(-307349204, -1)).toEqual(lg(0, 0))
      expect(lg(-17, -1) / lg(-44648, -1)).toEqual(lg(0, 0))
      expect(lg(39646, 0) / lg(-976, -1)).toEqual(lg(-40, -1))
      expect(lg(9, 0) / lg(315779722, 0)).toEqual(lg(0, 0))
      expect(lg(-2674, -1) / lg(-3051991, -1)).toEqual(lg(0, 0))
      expect(lg(-37697, -1) / lg(2015928, 0)).toEqual(lg(0, 0))
      expect(lg(-13, -1) / lg(-31, -1)).toEqual(lg(0, 0))
      expect(lg(6, 0) / lg(-334, -1)).toEqual(lg(0, 0))
      expect(lg(-15989, -1) / lg(-1918, -1)).toEqual(lg(8, 0))
      expect(lg(-113261535, -1) / lg(-12950, -1)).toEqual(lg(8746, 0))
      expect(lg(-6362112, -1) / lg(-115, -1)).toEqual(lg(55322, 0))
      expect(lg(455, 0) / lg(13919, 0)).toEqual(lg(0, 0))
      expect(lg(293468259, 0) / lg(8109, 0)).toEqual(lg(36190, 0))
      expect(lg(-48287007, -1) / lg(-27531186, -1)).toEqual(lg(1, 0))
      expect(lg(1048904, 0) / lg(3, 0)).toEqual(lg(349634, 0))
      expect(lg(-34, -1) / lg(3949717, 0)).toEqual(lg(0, 0))
      expect(lg(1449, 0) / lg(-983, -1)).toEqual(lg(-1, -1))
      expect(lg(18537151, 0) / lg(-1, -1)).toEqual(lg(-18537151, -1))
      expect(lg(14037, 0) / lg(23645, 0)).toEqual(lg(0, 0))
      expect(lg(1785, 0) / lg(-398, -1)).toEqual(lg(-4, -1))
      expect(lg(346, 0) / lg(2198158, 0)).toEqual(lg(0, 0))
      expect(lg(-3517419, -1) / lg(4381, 0)).toEqual(lg(-802, -1))
      expect(lg(6, 0) / lg(-1, -1)).toEqual(lg(-6, -1))
      expect(lg(-822, -1) / lg(-21, -1)).toEqual(lg(39, 0))
      expect(lg(3629, 0) / lg(282734, 0)).toEqual(lg(0, 0))
      expect(lg(-278856469, -1) / lg(3019, 0)).toEqual(lg(-92367, -1))
      expect(lg(-13, -1) / lg(37, 0)).toEqual(lg(0, 0))
      expect(lg(-4, -1) / lg(47150459, 0)).toEqual(lg(0, 0))
      expect(lg(-26, -1) / lg(-210691, -1)).toEqual(lg(0, 0))
      expect(lg(-21294, -1) / lg(156839456, 0)).toEqual(lg(0, 0))
      expect(lg(-5, -1) / lg(-25644, -1)).toEqual(lg(0, 0))
      expect(lg(-1009, -1) / lg(28100, 0)).toEqual(lg(0, 0))
      expect(lg(16282815, 0) / lg(-18989, -1)).toEqual(lg(-857, -1))
      expect(lg(-2201086, -1) / lg(276963, 0)).toEqual(lg(-7, -1))
      expect(lg(11412578, 0) / lg(-37989, -1)).toEqual(lg(-300, -1))
      expect(lg(8406900, 0) / lg(239727371, 0)).toEqual(lg(0, 0))
      expect(lg(-1, -1) / lg(-479069, -1)).toEqual(lg(0, 0))
      expect(lg(4, 0) / lg(-21776, -1)).toEqual(lg(0, 0))
      expect(lg(-16812960, -1) / lg(1, 0)).toEqual(lg(-16812960, -1))
      expect(lg(10873, 0) / lg(57145, 0)).toEqual(lg(0, 0))
      expect(lg(-1, -1) / lg(-7, -1)).toEqual(lg(0, 0))

      // int32 / int53
      expect(lg(-6975858, -1) / lg(42227636, 14)).toEqual(lg(0, 0))
      expect(lg(-1, -1) / lg(370644892, 82735)).toEqual(lg(0, 0))
      expect(lg(43, 0) / lg(-1602218381, 49)).toEqual(lg(0, 0))
      expect(lg(4063968, 0) / lg(973173538, 23810)).toEqual(lg(0, 0))
      expect(lg(-388987094, -1) / lg(-241988155, 1723)).toEqual(lg(0, 0))
      expect(lg(5939808, 0) / lg(-1882484681, 12)).toEqual(lg(0, 0))
      expect(lg(7, 0) / lg(-385609304, 1342)).toEqual(lg(0, 0))
      expect(lg(-1175803932, -1) / lg(297649103, 2408)).toEqual(lg(0, 0))
      expect(lg(464610492, 0) / lg(829919518, 2777)).toEqual(lg(0, 0))
      expect(lg(214483, 0) / lg(1502817270, 8078)).toEqual(lg(0, 0))

      // int32 / big
      expect(lg(211494165, 0) / lg(1365318534, 14804989)).toEqual(lg(0, 0))
      expect(lg(5353, 0) / lg(-1032992082, -394605386)).toEqual(lg(0, 0))
      expect(lg(2926, 0) / lg(26982087, -226814570)).toEqual(lg(0, 0))
      expect(lg(-6, -1) / lg(-1339229562, -580578613)).toEqual(lg(0, 0))
      expect(lg(-8, -1) / lg(-108570365, 4920615)).toEqual(lg(0, 0))
      expect(lg(-585878041, -1) / lg(551925027, -1296114209)).toEqual(lg(0, 0))
      expect(lg(-4, -1) / lg(474545806, 64068407)).toEqual(lg(0, 0))
      expect(lg(34, 0) / lg(-137127086, -18652281)).toEqual(lg(0, 0))
      expect(lg(785315, 0) / lg(-881374655, 29722835)).toEqual(lg(0, 0))
      expect(lg(713146, 0) / lg(1442548271, 2727525)).toEqual(lg(0, 0))

      // int53 / int32
      expect(lg(397755625, 53271) / lg(-395701427, -1)).toEqual(lg(-578207,
                                                                   -1))
      expect(lg(-1680186460, 2) / lg(3, 0)).toEqual(lg(-560062154, 0))
      expect(lg(1514942014, 56) / lg(3, 0)).toEqual(lg(-926675094, 18))
      expect(lg(713597492, 1154) / lg(-30524, -1)).toEqual(lg(-162400270, -1))
      expect(lg(2028377478, 1) / lg(-691707459, -1)).toEqual(lg(-9, -1))
      expect(lg(1387175556, 73) / lg(2332622, 0)).toEqual(lg(135006, 0))
      expect(lg(1756997282, 1397) / lg(-116, -1)).toEqual(lg(-200274428, -13))
      expect(lg(-1655346723, 0) / lg(2346, 0)).toEqual(lg(1125157, 0))
      expect(lg(198249458, 5686) / lg(24492497, 0)).toEqual(lg(997096, 0))
      expect(lg(873090497, 11162) / lg(-37, -1)).toEqual(lg(1369365326, -302))
      expect(lg(360057887, 3519) / lg(-6976354, -1)).toEqual(lg(-2166511, -1))
      expect(lg(1115898639, 48) / lg(-30, -1)).toEqual(lg(1680790298, -2))
      expect(lg(154624251, 955) / lg(935, 0)).toEqual(lg(92036331, 1))
      expect(lg(806830498, 1063) / lg(196698, 0)).toEqual(lg(23215066, 0))
      expect(lg(-220365267, 21359) / lg(-6938757, -1)).toEqual(lg(-13221428,
                                                                  -1))
      expect(lg(759822848, 648657) / lg(-323, -1)).toEqual(lg(-973041595,
                                                              -2009))
      expect(lg(-1180673754, 486098) / lg(293, 0)).toEqual(lg(171873494, 1659))
      expect(lg(1387172319, 769661) / lg(980, 0)).toEqual(lg(1583541189, 785))
      expect(lg(-305851327, 2) / lg(-13709, -1)).toEqual(lg(-917576, -1))
      expect(lg(577374631, 17) / lg(161353, 0)).toEqual(lg(456092, 0))
      expect(lg(809983260, 752) / lg(2, 0)).toEqual(lg(404991630, 376))
      expect(lg(495082175, 39) / lg(1, 0)).toEqual(lg(495082175, 39))
      expect(lg(1455620681, 30929) / lg(1461502, 0)).toEqual(lg(90893135, 0))
      expect(lg(1388707384, 34362) / lg(184688, 0)).toEqual(lg(799104733, 0))
      expect(lg(2105854641, 140021) / lg(-2, -1)).toEqual(lg(1094556328,
                                                             -70011))
      expect(lg(1310105355, 427420) / lg(271150, 0)).toEqual(lg(-1819673734,
                                                                1))
      expect(lg(-236557650, 35455) / lg(-7052, -1)).toEqual(lg(-119338773, -6))
      expect(lg(-1127581476, 0) / lg(96492, 0)).toEqual(lg(32825, 0))
      expect(lg(2004387480, 7243) / lg(-545624, -1)).toEqual(lg(-57018115, -1))
      expect(lg(381447319, 2213) / lg(-1597249, -1)).toEqual(lg(-5950946, -1))
      expect(lg(-1860702702, 12744) / lg(-3, -1)).toEqual(lg(-811421531,
                                                             -4249))
      expect(lg(-548164065, 6487) / lg(5877480, 0)).toEqual(lg(4741011, 0))
      expect(lg(-476290317, 131491) / lg(2874, 0)).toEqual(lg(-1064193809, 45))
      expect(lg(499912484, 1) / lg(21, 0)).toEqual(lg(228327608, 0))
      expect(lg(-1509435894, 8467) / lg(366943, 0)).toEqual(lg(99111506, 0))
      expect(lg(-1580093356, 5) / lg(-20, -1)).toEqual(lg(-1209485521, -1))
      expect(lg(1299112295, 55074) / lg(-739295, -1)).toEqual(lg(-319956618,
                                                                 -1))
      expect(lg(-1405948570, 43) / lg(-3015755, -1)).toEqual(lg(-62197, -1))
      expect(lg(1405130313, 57) / lg(27093454, 0)).toEqual(lg(9087, 0))
      expect(lg(-1804200888, 1989226) / lg(24722497, 0)).toEqual(lg(345582531,
                                                                    0))
      expect(lg(-1642507127, 886) / lg(-2672324, -1)).toEqual(lg(-1424974, -1))
      expect(lg(-1276796892, 35) / lg(77004, 0)).toEqual(lg(1991351, 0))
      expect(lg(-1200759296, 816) / lg(2939970, 0)).toEqual(lg(1193137, 0))
      expect(lg(399171813, 123795) / lg(926969, 0)).toEqual(lg(573585390, 0))
      expect(lg(1649267984, 229752) / lg(-244, -1)).toEqual(lg(1683063904,
                                                               -942))
      expect(lg(-387146187, 7364) / lg(-5255245, -1)).toEqual(lg(-6019138, -1))
      expect(lg(149703916, 19121) / lg(660, 0)).toEqual(lg(-123416174, 28))
      expect(lg(-1582312743, 7920) / lg(-835168, -1)).toEqual(lg(-40732946,
                                                                 -1))
      expect(lg(1431643220, 596) / lg(2, 0)).toEqual(lg(715821610, 298))
      expect(lg(-1717918737, 8458) / lg(-63727, -1)).toEqual(lg(-570078780,
                                                                -1))

      // int53 / int53
      expect(lg(-1232398900, 28871) / lg(13989713, 22345)).toEqual(lg(1, 0))
      expect(lg(-916994839, 12266) / lg(1713571419, 15301)).toEqual(lg(0, 0))
      expect(lg(1133414946, 229) / lg(256531666, 7)).toEqual(lg(32, 0))
      expect(lg(134792921, 3907) / lg(-1656790262, 10)).toEqual(lg(368, 0))
      expect(lg(1532393452, 52260) / lg(-701373106, 31864)).toEqual(lg(1, 0))
      expect(lg(193990135, 1460) / lg(867607428, 6918)).toEqual(lg(0, 0))
      expect(lg(867672590, 1) / lg(-1315044816, 987593)).toEqual(lg(0, 0))
      expect(lg(-978844610, 2) / lg(720710523, 209)).toEqual(lg(0, 0))
      expect(lg(-297570329, 1) / lg(-2127979750, 195738)).toEqual(lg(0, 0))
      expect(lg(-1035330427, 5) / lg(-2091513925, 70)).toEqual(lg(0, 0))
      expect(lg(1037142987, 15) / lg(-485498951, 30819)).toEqual(lg(0, 0))
      expect(lg(744551901, 15) / lg(-604684037, 1587)).toEqual(lg(0, 0))
      expect(lg(1341710951, 232724) / lg(1864827988, 3)).toEqual(lg(67766, 0))
      expect(lg(-409318148, 157818) / lg(517165426, 227)).toEqual(lg(694, 0))
      expect(lg(1908192460, 110512) / lg(-61974596, 95795)).toEqual(lg(1, 0))
      expect(lg(946490654, 498) / lg(-1889366637, 1163)).toEqual(lg(0, 0))
      expect(lg(1765257877, 34422) / lg(728455544, 2851)).toEqual(lg(12, 0))
      expect(lg(-1725136864, 84) / lg(1122821677, 14720)).toEqual(lg(0, 0))
      expect(lg(1854803780, 2) / lg(-302860117, 1)).toEqual(lg(1, 0))
      expect(lg(380756581, 107) / lg(-806772264, 0)).toEqual(lg(131, 0))
      expect(lg(1868292481, 1134) / lg(691774521, 33775)).toEqual(lg(0, 0))
      expect(lg(-1515810361, 98) / lg(2038289788, 198)).toEqual(lg(0, 0))
      expect(lg(-1943767475, 31777) / lg(-1513506636, 100)).toEqual(lg(315, 0))
      expect(lg(1508904915, 18) / lg(1834666309, 976)).toEqual(lg(0, 0))
      expect(lg(1430753947, 3772) / lg(-1853122145, 3615)).toEqual(lg(1, 0))
      expect(lg(-1654852151, 1195820) / lg(-2100231332, 0)).toEqual(lg(2340149,
                                                                       0))
      expect(lg(1011710080, 18) / lg(-616681449, 57)).toEqual(lg(0, 0))
      expect(lg(-495370429, 356832) / lg(-34555439, 25233)).toEqual(lg(14, 0))
      expect(lg(744211838, 511) / lg(-475809581, 3)).toEqual(lg(131, 0))
      expect(lg(1135128265, 67) / lg(163864249, 972)).toEqual(lg(0, 0))
      expect(lg(954856869, 5120) / lg(1474096435, 3606)).toEqual(lg(1, 0))
      expect(lg(1544045220, 1) / lg(85376495, 2353)).toEqual(lg(0, 0))
      expect(lg(1367437144, 53) / lg(2010850631, 6)).toEqual(lg(8, 0))
      expect(lg(-1398730804, 13) / lg(-2055007528, 52)).toEqual(lg(0, 0))
      expect(lg(1598156017, 13) / lg(-1006929331, 160)).toEqual(lg(0, 0))
      expect(lg(738323529, 41) / lg(-1508093984, 10361)).toEqual(lg(0, 0))
      expect(lg(-1788797806, 31) / lg(588557582, 575930)).toEqual(lg(0, 0))
      expect(lg(-913009845, 1002) / lg(204577043, 13)).toEqual(lg(76, 0))
      expect(lg(1908599465, 6) / lg(1058868127, 3383)).toEqual(lg(0, 0))
      expect(lg(-634312634, 75) / lg(-850292534, 332928)).toEqual(lg(0, 0))
      expect(lg(-1679695022, 148) / lg(-1395453213, 912)).toEqual(lg(0, 0))
      expect(lg(456310936, 71) / lg(487720864, 1590813)).toEqual(lg(0, 0))
      expect(lg(-1724925398, 0) / lg(-273170277, 38)).toEqual(lg(0, 0))
      expect(lg(-6742076, 15) / lg(192793866, 175)).toEqual(lg(0, 0))
      expect(lg(337939061, 2094205) / lg(880147944, 41142)).toEqual(lg(50, 0))
      expect(lg(-998413092, 0) / lg(-1758700885, 29)).toEqual(lg(0, 0))
      expect(lg(1986052307, 3) / lg(-2092246422, 47)).toEqual(lg(0, 0))
      expect(lg(-109615093, 1) / lg(-2066395387, 20016)).toEqual(lg(0, 0))
      expect(lg(-1147373454, 901) / lg(313439710, 7)).toEqual(lg(127, 0))
      expect(lg(-792716629, 66379) / lg(2017337246, 250513)).toEqual(lg(0, 0))

      // int53 / big
      expect(lg(291278707, 13808) / lg(941639833, -14430466)).toEqual(lg(0, 0))
      expect(lg(-857819626, 204588) / lg(-1909684886, -709519130)).toEqual(lg(
          0, 0))
      expect(lg(-978105991, 7435) / lg(-306472275, 158306339)).toEqual(lg(0,
                                                                          0))
      expect(lg(75049741, 248171) / lg(-1574105194, 64879257)).toEqual(lg(0,
                                                                          0))
      expect(lg(136051120, 621) / lg(-1671784392, 102642869)).toEqual(lg(0, 0))
      expect(lg(-448460356, 2858) / lg(71740423, -16715717)).toEqual(lg(0, 0))
      expect(lg(-1266403435, 2) / lg(-1022999838, 25812014)).toEqual(lg(0, 0))
      expect(lg(552733494, 22) / lg(241731505, -33191170)).toEqual(lg(0, 0))
      expect(lg(1366167794, 115591) / lg(191854687, -2136953)).toEqual(lg(0,
                                                                          0))
      expect(lg(1329114439, 80951) / lg(-51187101, 1471052997)).toEqual(lg(0,
                                                                           0))

      // big / int32
      expect(lg(-1495113094, 168518701) / lg(27633219, 0)).toEqual(lg(
          422668131, 6))
      expect(lg(-1211847018, -609137255) / lg(-2976, -1)).toEqual(lg(932715295,
                                                                     204683))
      expect(lg(-457166837, -15040808) / lg(-340331202, -1)).toEqual(lg(
          189814434, 0))
      expect(lg(-104570473, -117704108) / lg(104, 0)).toEqual(lg(-1116045071,
                                                                 -1131771))
      expect(lg(453828098, -10187034) / lg(-707, -1)).toEqual(lg(-784306379,
                                                                 14408))
      expect(lg(1911518920, 168201762) / lg(84, 0)).toEqual(lg(-284027201,
                                                               2002401))
      expect(lg(610589058, 36481453) / lg(-30381877, -1)).toEqual(lg(
          -862273257, -2))
      expect(lg(410700182, 503953004) / lg(-7181145, -1)).toEqual(lg(
          -761280647, -71))
      expect(lg(194917334, -8806907) / lg(3471, 0)).toEqual(lg(-1212582262,
                                                               -2538))
      expect(lg(852311155, 9671380) / lg(2048884, 0)).toEqual(lg(-1201233065,
                                                                 4))
      expect(lg(-1028681544, 4163983) / lg(13506586, 0)).toEqual(lg(1324107666,
                                                                    0))
      expect(lg(-708734088, 12722223) / lg(2, 0)).toEqual(lg(-354367044,
                                                             6361111))
      expect(lg(1693696214, 18402294) / lg(-241, -1)).toEqual(lg(-292170842,
                                                                 -76359))
      expect(lg(-1932788158, 206747917) / lg(-5, -1)).toEqual(lg(2104544550,
                                                                 -41349584))
      expect(lg(1427262980, -60732866) / lg(3409, 0)).toEqual(lg(-1928473941,
                                                                 -17816))
      expect(lg(-677896940, 2512898) / lg(-3693, -1)).toEqual(lg(-1929237164,
                                                                 -681))
      expect(lg(-926729663, -9677195) / lg(279372, 0)).toEqual(lg(1550060300,
                                                                  -35))
      expect(lg(-405257725, -2271799) / lg(-3770075, -1)).toEqual(lg(
          -1706875941, 0))
      expect(lg(-1893733008, -6491069) / lg(-595, -1)).toEqual(lg(1540708852,
                                                                  10909))
      expect(lg(-1343018634, -2584815) / lg(7233, 0)).toEqual(lg(-1563665409,
                                                                 -358))
      expect(lg(-1224507547, 122799570) / lg(-328, -1)).toEqual(lg(278715917,
                                                                   -374389))
      expect(lg(-2082712791, -15998594) / lg(-48337828, -1)).toEqual(lg(
          1421525100, 0))
      expect(lg(-2147318181, -32907160) / lg(15, 0)).toEqual(lg(1574832373,
                                                                -2193811))
      expect(lg(1074158039, 118905936) / lg(-1930, -1)).toEqual(lg(-1260116915,
                                                                   -61610))
      expect(lg(1270835097, -2201288) / lg(141, 0)).toEqual(lg(130856059,
                                                               -15612))
      expect(lg(320077861, -446108079) / lg(-189997, -1)).toEqual(lg(
          -110248455, 2347))
      expect(lg(1075676628, 54005547) / lg(440453, 0)).toEqual(lg(-1659387265,
                                                                  122))
      expect(lg(-1800001035, 54578889) / lg(2877683, 0)).toEqual(lg(-144903831,
                                                                    18))
      expect(lg(-654120591, 33364168) / lg(-1393, -1)).toEqual(lg(-1312994937,
                                                                  -23952))
      expect(lg(302695822, -2432394) / lg(58667176, 0)).toEqual(lg(-178073210,
                                                                   -1))
      expect(lg(523451067, -54366538) / lg(-382038, -1)).toEqual(lg(1316938460,
                                                                    142))
      expect(lg(-78968601, 52670560) / lg(3, 0)).toEqual(lg(-1457978633,
                                                            17556853))
      expect(lg(-773046192, -1010259224) / lg(-2, -1)).toEqual(lg(-1760960552,
                                                                  505129611))
      expect(lg(1515488136, -21874592) / lg(-9452, -1)).toEqual(lg(1210355204,
                                                                   2314))
      expect(lg(-1043595428, -1725615548) / lg(-2, -1)).toEqual(lg(-1625685934,
                                                                   862807773))
      expect(lg(-1217231978, 1516494005) / lg(375097846, 0)).toEqual(lg(
          184379181, 4))
      expect(lg(-1873413508, -236381131) / lg(-816152673, -1)).toEqual(lg(
          1243945230, 0))
      expect(lg(265593875, 26513736) / lg(-30289, -1)).toEqual(lg(-1540093941,
                                                                  -876))
      expect(lg(106204837, -839801203) / lg(-1544, -1)).toEqual(lg(-1304692919,
                                                                   543912))
      expect(lg(815576040, -55524975) / lg(-2331779, -1)).toEqual(lg(
          -806250591, 23))
      expect(lg(-2053929476, -1795047022) / lg(720742474, 0)).toEqual(lg(
          -2106907248, -3))
      expect(lg(1552099699, 65024502) / lg(-525272, -1)).toEqual(lg(893100234,
                                                                    -124))
      expect(lg(-194253417, -12405472) / lg(-9879, -1)).toEqual(lg(-1109915706,
                                                                   1255))
      expect(lg(412309016, 112344162) / lg(154800321, 0)).toEqual(lg(
          -1177955013, 0))
      expect(lg(343591192, -102047607) / lg(2, 0)).toEqual(lg(-1975688052,
                                                              -51023804))
      expect(lg(1756765281, 8058834) / lg(-26, -1)).toEqual(lg(-728332094,
                                                               -309956))
      expect(lg(1762668787, -960735493) / lg(-782994, -1)).toEqual(lg(10173004,
                                                                      1227))
      expect(lg(1523935530, -109345767) / lg(-18963, -1)).toEqual(lg(
          1157067129, 5766))
      expect(lg(-1256703941, 1438436241) / lg(34, 0)).toEqual(lg(1226263794,
                                                                 42306948))
      expect(lg(-444491016, -6150392) / lg(14, 0)).toEqual(lg(1502167534,
                                                              -439314))

      // big / int53
      expect(lg(-1883357942, 360257606) / lg(1478768728, 4075)).toEqual(lg(
          88399, 0))
      expect(lg(-1991900757, -48856999) / lg(-1087694619, 1074)).toEqual(lg(
          -45459, -1))
      expect(lg(518426119, 218946975) / lg(-808940852, 49)).toEqual(lg(4395497,
                                                                       0))
      expect(lg(-946567777, 600381050) / lg(-1165957306, 187)).toEqual(lg(
          3198134, 0))
      expect(lg(257885254, 845979705) / lg(792779187, 1798424)).toEqual(lg(470,
                                                                           0))
      expect(lg(1278680372, 6485140) / lg(1376461023, 70263)).toEqual(lg(92,
                                                                         0))
      expect(lg(1445602310, 420550818) / lg(1397186900, 2507)).toEqual(lg(
          167728, 0))
      expect(lg(1822058703, 522114268) / lg(1355449555, 20)).toEqual(lg(
          25700177, 0))
      expect(lg(532749659, -130990067) / lg(-1474774415, 3)).toEqual(lg(
          -35822646, -1))
      expect(lg(1329707986, -2121642) / lg(-63366094, 6086)).toEqual(lg(-348,
                                                                        -1))
      expect(lg(1028585430, -118524228) / lg(1655878874, 54392)).toEqual(lg(
          -2179, -1))
      expect(lg(203502475, 42252914) / lg(36519512, 35581)).toEqual(lg(1187,
                                                                       0))
      expect(lg(341088508, 35053507) / lg(917391400, 10874)).toEqual(lg(3223,
                                                                        0))
      expect(lg(1454135412, 69933847) / lg(-162213744, 2)).toEqual(lg(23608500,
                                                                      0))
      expect(lg(1674604578, 10565585) / lg(1932570831, 1)).toEqual(lg(7286803,
                                                                      0))
      expect(lg(-1910257093, -16610962) / lg(-640594227, 120)).toEqual(lg(
          -137450, -1))
      expect(lg(1080864951, 17606069) / lg(-1542196664, 153)).toEqual(lg(
          114592, 0))
      expect(lg(-1419644278, 13937517) / lg(-919779905, 227700)).toEqual(lg(61,
                                                                            0))
      expect(lg(-1958380469, -855713410) / lg(1631833189, 3459)).toEqual(lg(
          -247360, -1))
      expect(lg(1951473618, -4122677) / lg(-899615165, 66)).toEqual(lg(-61725,
                                                                       -1))
      expect(lg(1521276132, 182952467) / lg(346742782, 82171)).toEqual(lg(2226,
                                                                          0))
      expect(lg(-1003647481, -7808320) / lg(-228453385, 7826)).toEqual(lg(-997,
                                                                          -1))
      expect(lg(-875689390, 4467236) / lg(-590010750, 120938)).toEqual(lg(36,
                                                                          0))
      expect(lg(1189085620, 611543209) / lg(1619962756, 10919)).toEqual(lg(
          56005, 0))
      expect(lg(-1072173311, -18503031) / lg(1971480267, 205)).toEqual(lg(
          -90057, -1))
      expect(lg(767303802, -3407362) / lg(-339044225, 352939)).toEqual(lg(-9,
                                                                          -1))
      expect(lg(427996893, 482974074) / lg(-736462105, 7759)).toEqual(lg(62240,
                                                                         0))
      expect(lg(842450255, -4396651) / lg(859272322, 2477)).toEqual(lg(-1774,
                                                                       -1))
      expect(lg(1640433988, -2618618) / lg(302672196, 17)).toEqual(lg(-153400,
                                                                      -1))
      expect(lg(-361322518, 63967358) / lg(-1922353888, 29810)).toEqual(lg(
          2145, 0))
      expect(lg(-1774479550, 43276853) / lg(472456506, 408)).toEqual(lg(106042,
                                                                        0))
      expect(lg(-1756338345, -38928780) / lg(283612141, 102)).toEqual(lg(
          -381407, -1))
      expect(lg(-495049835, 37161263) / lg(-2052025512, 30)).toEqual(lg(
          1217514, 0))
      expect(lg(1606509747, -10876159) / lg(1068727249, 635715)).toEqual(lg(
          -17, -1))
      expect(lg(-1857686692, 1918485655) / lg(454913535, 393)).toEqual(lg(
          4880327, 0))
      expect(lg(-502107392, -511268482) / lg(-1118977400, 499)).toEqual(lg(
          -1023070, -1))
      expect(lg(-909192131, 45216813) / lg(1442986382, 102923)).toEqual(lg(439,
                                                                           0))
      expect(lg(259184089, 14858724) / lg(-671961291, 6)).toEqual(lg(2171202,
                                                                     0))
      expect(lg(1737846340, -614952982) / lg(1379175047, 115)).toEqual(lg(
          -5332527, -1))
      expect(lg(-406629212, -528407898) / lg(973577032, 1214)).toEqual(lg(
          -435180, -1))
      expect(lg(-597461306, 538945619) / lg(-1867966522, 19360)).toEqual(lg(
          27837, 0))
      expect(lg(-1906945200, -371170760) / lg(151858506, 936902)).toEqual(lg(
          -396, -1))
      expect(lg(-1366510, -207691415) / lg(-872314548, 1)).toEqual(lg(
          -115583279, -1))
      expect(lg(-1280665444, -104856505) / lg(1964875665, 15)).toEqual(lg(
          -6783543, -1))
      expect(lg(897601097, -1352132581) / lg(-328204224, 0)).toEqual(lg(
          -1464006069, -1))
      expect(lg(-496529216, 32992512) / lg(-668292521, 2)).toEqual(lg(11599107,
                                                                      0))
      expect(lg(1819966537, 311969505) / lg(-879441284, 370147)).toEqual(lg(
          842, 0))
      expect(lg(433235702, 408255734) / lg(573404298, 9382)).toEqual(lg(43514,
                                                                        0))
      expect(lg(1693350453, -4127304) / lg(-1671879801, 17931)).toEqual(lg(
          -230, -1))
      expect(lg(-492682302, 64433722) / lg(-1408841594, 258)).toEqual(lg(
          249094, 0))

      // big / big
      expect(lg(1450795502, -706709103) / lg(742056886, 64843937)).toEqual(lg(
          -10, -1))
      expect(lg(-392893244, 72026637) / lg(1419676270, 875736789)).toEqual(lg(
          0, 0))
      expect(lg(-1861146463, 8382761) / lg(-724412724, -3000735)).toEqual(lg(
          -2, -1))
      expect(lg(1373482238, 23344691) / lg(1835527248, -294342355)).toEqual(lg(
          0, 0))
      expect(lg(1956796392, 107480459) / lg(-560958184, -2839471)).toEqual(lg(
          -37, -1))
      expect(lg(422228275, 30436377) / lg(-2023395425, 8226201)).toEqual(lg(3,
                                                                            0))
      expect(lg(1747624836, -215352612) / lg(-1349940168, 58723974))
        .toEqual(lg(-3, -1))
      expect(lg(-583006891, 16111063) / lg(1853686630, 5479773)).toEqual(lg(2,
                                                                            0))
      expect(lg(1498104050, 7322401) / lg(-407388940, 2141575618)).toEqual(lg(
          0, 0))
      expect(lg(1943726712, 869895175) / lg(-627430826, 169278540)).toEqual(lg(
          5, 0))
      expect(lg(1872895982, 98966340) / lg(1347573135, 529034148)).toEqual(lg(
          0, 0))
      expect(lg(16010610, 187913494) / lg(-848952152, -81951424)).toEqual(lg(
          -2, -1))
      expect(lg(830929771, -4393252) / lg(1829525088, 52659897)).toEqual(lg(0,
                                                                            0))
      expect(lg(-2093526384, 133319293) / lg(-464927151, 6049576)).toEqual(lg(
          22, 0))
      expect(lg(1056318793, 13467735) / lg(1970348162, -672507521)).toEqual(lg(
          0, 0))
      expect(lg(-28853693, -169722715) / lg(-83877421, 770900857)).toEqual(lg(
          0, 0))
      expect(lg(1743854071, -302158995) / lg(80117835, 11113120)).toEqual(lg(
          -27, -1))
      expect(lg(635796581, -146765250) / lg(441664676, 23716738)).toEqual(lg(
          -6, -1))
      expect(lg(-1048312948, -37662905) / lg(1319664078, 208772026))
        .toEqual(lg(0, 0))
      expect(lg(-784292680, -14102823) / lg(2037268040, 744987722)).toEqual(lg(
          0, 0))
      expect(lg(-1116104092, -2073525743) / lg(1766685765, -11731135))
        .toEqual(lg(176, 0))
      expect(lg(-1991687284, 19448294) / lg(-1731357606, -202272807))
        .toEqual(lg(0, 0))
      expect(lg(-2042068328, -52956481) / lg(370482897, -7759903)).toEqual(lg(
          6, 0))
      expect(lg(334395247, 1906338595) / lg(342095090, 1248830168)).toEqual(lg(
          1, 0))
      expect(lg(-309616588, 44123460) / lg(2040055580, -476494291)).toEqual(lg(
          0, 0))
      expect(lg(137178123, 36336421) / lg(-360221107, -515689970)).toEqual(lg(
          0, 0))
      expect(lg(-422856762, -16760844) / lg(-334268074, -43984484)).toEqual(lg(
          0, 0))
      expect(lg(-24820293, 25823996) / lg(390711705, 288223876)).toEqual(lg(0,
                                                                            0))
      expect(lg(1170265006, 2998984) / lg(-134995170, -2123267074)).toEqual(lg(
          0, 0))
      expect(lg(-1501380980, -6088910) / lg(-1175861016, -56027408))
        .toEqual(lg(0, 0))
      expect(lg(307880183, 196786483) / lg(-1107761890, -3480429)).toEqual(lg(
          -56, -1))
      expect(lg(-588606997, -37732967) / lg(-1124435958, -77404915))
        .toEqual(lg(0, 0))
      expect(lg(90560661, 990295925) / lg(731139348, 9165999)).toEqual(lg(108,
                                                                          0))
      expect(lg(46312609, -28251908) / lg(1279863155, -519028300)).toEqual(lg(
          0, 0))
      expect(lg(1123427761, 55212863) / lg(-1081219733, 233090714)).toEqual(lg(
          0, 0))
      expect(lg(1447869812, -3646400) / lg(-1237950546, -27122943)).toEqual(lg(
          0, 0))
      expect(lg(-1399920635, 110072031) / lg(-398678056, -8069387)).toEqual(lg(
          -13, -1))
      expect(lg(513704441, 14319377) / lg(-796719013, 260081997)).toEqual(lg(
          0, 0))
      expect(lg(166886349, -190148673) / lg(68245235, -21656365)).toEqual(lg(
          8, 0))
      expect(lg(-1594024534, -144937584) / lg(177399758, 200473672))
        .toEqual(lg(0, 0))
      expect(lg(447753993, -23591908) / lg(1399162166, 12505918)).toEqual(lg(
          -1, -1))
      expect(lg(1500283330, 5361180) / lg(348398676, 156400271)).toEqual(lg(0,
                                                                            0))
      expect(lg(-216115001, 670826068) / lg(1759253954, -470062110))
        .toEqual(lg(-1, -1))
      expect(lg(-1251659767, 18831569) / lg(-669341445, -34474821)).toEqual(lg(
          0, 0))
      expect(lg(817032953, 218701872) / lg(-176557210, 6899121)).toEqual(lg(31,
                                                                            0))
      expect(lg(1365998269, 613319842) / lg(319204438, -30758748)).toEqual(lg(
          -19, -1))
      expect(lg(-428500325, 6610536) / lg(-46648893, -105360271)).toEqual(lg(
          0, 0))
      expect(lg(784528299, -6958267) / lg(1370662827, -774132635)).toEqual(lg(
          0, 0))
      expect(lg(-769114167, 137614183) / lg(-929091402, -67103082)).toEqual(lg(
          -2, -1))
      expect(lg(1810734914, 124115952) / lg(1149563530, 15197570)).toEqual(lg(
          8, 0))
    }

    it("modulo %") {
      expectThrows[ArithmeticException](lg(0) % lg(0))
      expectThrows[ArithmeticException](lg(5, 0) % lg(0))
      expectThrows[ArithmeticException](lg(0, 5) % lg(0))
      expectThrows[ArithmeticException](lg(-1) % lg(0))
      expectThrows[ArithmeticException](lg(-1, 0) % lg(0))

      expect(IntMinVal % lg(-1)).toEqual(lg(0))
      expect(IntMinVal % IntMaxValPlus1).toEqual(lg(0))
      expect(IntMaxValPlus1 % lg(-1)).toEqual(lg(0))
      expect(IntMaxValPlus1 % IntMinVal).toEqual(lg(0))

      expect(MaxVal % lg(-1)).toEqual(lg(0))
      expect(MinVal % lg(1)).toEqual(lg(0))
      expect(MinVal % lg(-1)).toEqual(lg(0))

      expect(MaxVal % MinVal).toEqual(lg(-1, 2147483647))
      expect(MaxVal % MaxVal).toEqual(lg(0))
      expect(MinVal % MinVal).toEqual(lg(0))
      expect(MinVal % MaxVal).toEqual(lg(-1))

      // int32 % int32
      expect(lg(880, 0) % lg(-219594, -1)).toEqual(lg(880, 0))
      expect(lg(-49125, -1) % lg(98, 0)).toEqual(lg(-27, -1))
      expect(lg(-1922504, -1) % lg(4195, 0)).toEqual(lg(-1194, -1))
      expect(lg(3, 0) % lg(7963, 0)).toEqual(lg(3, 0))
      expect(lg(-626, -1) % lg(-484628621, -1)).toEqual(lg(-626, -1))
      expect(lg(11315, 0) % lg(-3914076, -1)).toEqual(lg(11315, 0))
      expect(lg(15712341, 0) % lg(-1045740, -1)).toEqual(lg(26241, 0))
      expect(lg(-855439, -1) % lg(5213, 0)).toEqual(lg(-507, -1))
      expect(lg(-101026259, -1) % lg(-500, -1)).toEqual(lg(-259, -1))
      expect(lg(27720977, 0) % lg(-42317657, -1)).toEqual(lg(27720977, 0))
      expect(lg(25954, 0) % lg(-3, -1)).toEqual(lg(1, 0))
      expect(lg(338447650, 0) % lg(-8505730, -1)).toEqual(lg(6724180, 0))
      expect(lg(23967, 0) % lg(-13479, -1)).toEqual(lg(10488, 0))
      expect(lg(885202, 0) % lg(-3, -1)).toEqual(lg(1, 0))
      expect(lg(692795590, 0) % lg(-10, -1)).toEqual(lg(0, 0))
      expect(lg(-1, -1) % lg(156, 0)).toEqual(lg(-1, -1))
      expect(lg(388, 0) % lg(189523294, 0)).toEqual(lg(388, 0))
      expect(lg(352, 0) % lg(-3257, -1)).toEqual(lg(352, 0))
      expect(lg(-9, -1) % lg(14653, 0)).toEqual(lg(-9, -1))
      expect(lg(-258745, -1) % lg(8, 0)).toEqual(lg(-1, -1))
      expect(lg(-206976653, -1) % lg(34321, 0)).toEqual(lg(-21023, -1))
      expect(lg(-1, -1) % lg(-971, -1)).toEqual(lg(-1, -1))
      expect(lg(59, 0) % lg(388, 0)).toEqual(lg(59, 0))
      expect(lg(-7, -1) % lg(1, 0)).toEqual(lg(0, 0))
      expect(lg(77, 0) % lg(13, 0)).toEqual(lg(12, 0))
      expect(lg(224246, 0) % lg(719055, 0)).toEqual(lg(224246, 0))
      expect(lg(-61296, -1) % lg(-135723660, -1)).toEqual(lg(-61296, -1))
      expect(lg(6897809, 0) % lg(793543, 0)).toEqual(lg(549465, 0))
      expect(lg(45, 0) % lg(984210147, 0)).toEqual(lg(45, 0))
      expect(lg(-64, -1) % lg(1, 0)).toEqual(lg(0, 0))
      expect(lg(379611734, 0) % lg(4, 0)).toEqual(lg(2, 0))
      expect(lg(0, 0) % lg(-263, -1)).toEqual(lg(0, 0))
      expect(lg(29, 0) % lg(-117, -1)).toEqual(lg(29, 0))
      expect(lg(245094, 0) % lg(-70, -1)).toEqual(lg(24, 0))
      expect(lg(0, 0) % lg(5, 0)).toEqual(lg(0, 0))
      expect(lg(2, 0) % lg(47787927, 0)).toEqual(lg(2, 0))
      expect(lg(-124, -1) % lg(-22714040, -1)).toEqual(lg(-124, -1))
      expect(lg(412, 0) % lg(-17176, -1)).toEqual(lg(412, 0))
      expect(lg(-11860, -1) % lg(9506787, 0)).toEqual(lg(-11860, -1))
      expect(lg(-31, -1) % lg(-1544676, -1)).toEqual(lg(-31, -1))
      expect(lg(-1990315281, -1) % lg(-7, -1)).toEqual(lg(-3, -1))
      expect(lg(99, 0) % lg(-277, -1)).toEqual(lg(99, 0))
      expect(lg(-29227, -1) % lg(-161, -1)).toEqual(lg(-86, -1))
      expect(lg(106, 0) % lg(-47032956, -1)).toEqual(lg(106, 0))
      expect(lg(18, 0) % lg(510836179, 0)).toEqual(lg(18, 0))
      expect(lg(3543112, 0) % lg(10, 0)).toEqual(lg(2, 0))
      expect(lg(3547603, 0) % lg(-1506666, -1)).toEqual(lg(534271, 0))
      expect(lg(-16361, -1) % lg(10637613, 0)).toEqual(lg(-16361, -1))
      expect(lg(606879016, 0) % lg(-16, -1)).toEqual(lg(8, 0))
      expect(lg(-1, -1) % lg(46424570, 0)).toEqual(lg(-1, -1))

      // int32 % int53
      expect(lg(-3, -1) % lg(206801065, 1)).toEqual(lg(-3, -1))
      expect(lg(-57756, -1) % lg(-1211050362, 13)).toEqual(lg(-57756, -1))
      expect(lg(0, 0) % lg(-475702596, 10040)).toEqual(lg(0, 0))
      expect(lg(423524, 0) % lg(-2084961556, 16)).toEqual(lg(423524, 0))
      expect(lg(38317, 0) % lg(-1699004544, 24)).toEqual(lg(38317, 0))
      expect(lg(60291, 0) % lg(-458289291, 56)).toEqual(lg(60291, 0))
      expect(lg(1, 0) % lg(-1247681936, 1229953)).toEqual(lg(1, 0))
      expect(lg(296788, 0) % lg(183245860, 52)).toEqual(lg(296788, 0))
      expect(lg(-2005515, -1) % lg(331735459, 17)).toEqual(lg(-2005515, -1))
      expect(lg(-179812, -1) % lg(-853047550, 5154)).toEqual(lg(-179812, -1))
      expect(lg(-3678, -1) % lg(1751271067, 243605)).toEqual(lg(-3678, -1))
      expect(lg(-93867, -1) % lg(-1925367590, 42)).toEqual(lg(-93867, -1))
      expect(lg(7600917, 0) % lg(-1807424604, 95574)).toEqual(lg(7600917, 0))
      expect(lg(300012, 0) % lg(1951216728, 101)).toEqual(lg(300012, 0))
      expect(lg(-6347, -1) % lg(-438713154, 23)).toEqual(lg(-6347, -1))
      expect(lg(-41, -1) % lg(-1211982116, 459)).toEqual(lg(-41, -1))
      expect(lg(3425, 0) % lg(-1580976156, 2)).toEqual(lg(3425, 0))
      expect(lg(-25, -1) % lg(200240265, 25993)).toEqual(lg(-25, -1))
      expect(lg(-8303, -1) % lg(1353761386, 1921)).toEqual(lg(-8303, -1))
      expect(lg(274032571, 0) % lg(1455543028, 255)).toEqual(lg(274032571, 0))
      expect(lg(-3, -1) % lg(1143775281, 729)).toEqual(lg(-3, -1))
      expect(lg(-1124428, -1) % lg(-521284400, 339)).toEqual(lg(-1124428, -1))
      expect(lg(-2, -1) % lg(-303859962, 2524)).toEqual(lg(-2, -1))
      expect(lg(1, 0) % lg(-402000545, 1)).toEqual(lg(1, 0))
      expect(lg(107013504, 0) % lg(157604607, 3)).toEqual(lg(107013504, 0))
      expect(lg(4976822, 0) % lg(-2046021074, 2230)).toEqual(lg(4976822, 0))
      expect(lg(-1, -1) % lg(-306200858, 41)).toEqual(lg(-1, -1))
      expect(lg(80396, 0) % lg(-409002766, 13)).toEqual(lg(80396, 0))
      expect(lg(937638, 0) % lg(-697219650, 26)).toEqual(lg(937638, 0))
      expect(lg(756, 0) % lg(-948806692, 1700920)).toEqual(lg(756, 0))
      expect(lg(5, 0) % lg(646021801, 21350)).toEqual(lg(5, 0))
      expect(lg(262831839, 0) % lg(1086270794, 10633)).toEqual(lg(262831839,
                                                                  0))
      expect(lg(-2146273993, -1) % lg(-1539129401, 0)).toEqual(lg(-2146273993,
                                                                  -1))
      expect(lg(59799, 0) % lg(1910837623, 102082)).toEqual(lg(59799, 0))
      expect(lg(-5347, -1) % lg(1965292799, 18)).toEqual(lg(-5347, -1))
      expect(lg(926, 0) % lg(1939309159, 104206)).toEqual(lg(926, 0))
      expect(lg(1, 0) % lg(1651864405, 1233)).toEqual(lg(1, 0))
      expect(lg(334, 0) % lg(581635234, 20)).toEqual(lg(334, 0))
      expect(lg(-61747, -1) % lg(-842193425, 1497)).toEqual(lg(-61747, -1))
      expect(lg(-1, -1) % lg(758739794, 79508)).toEqual(lg(-1, -1))
      expect(lg(59605313, 0) % lg(-1162319751, 0)).toEqual(lg(59605313, 0))
      expect(lg(12267518, 0) % lg(1340161110, 568352)).toEqual(lg(12267518, 0))
      expect(lg(19230695, 0) % lg(1844291137, 21)).toEqual(lg(19230695, 0))
      expect(lg(3950296, 0) % lg(-848670202, 243)).toEqual(lg(3950296, 0))
      expect(lg(503276, 0) % lg(-1756374670, 1)).toEqual(lg(503276, 0))
      expect(lg(30880536, 0) % lg(-1380766565, 51064)).toEqual(lg(30880536, 0))
      expect(lg(5659804, 0) % lg(-725339057, 1)).toEqual(lg(5659804, 0))
      expect(lg(11882277, 0) % lg(243727355, 7)).toEqual(lg(11882277, 0))
      expect(lg(371783010, 0) % lg(630143580, 14001)).toEqual(lg(371783010, 0))
      expect(lg(840, 0) % lg(-1719362098, 109)).toEqual(lg(840, 0))

      // int32 % big
      expect(lg(-267334310, -1) % lg(1537718115, -134598983)).toEqual(lg(
          -267334310, -1))
      expect(lg(57, 0) % lg(-1668867109, -10100325)).toEqual(lg(57, 0))
      expect(lg(30332, 0) % lg(-615310153, -90004876)).toEqual(lg(30332, 0))
      expect(lg(187, 0) % lg(-590535223, 8244144)).toEqual(lg(187, 0))
      expect(lg(-2, -1) % lg(2125719729, 390762530)).toEqual(lg(-2, -1))
      expect(lg(-4252915, -1) % lg(2070489053, 23484863)).toEqual(lg(-4252915,
                                                                     -1))
      expect(lg(-2, -1) % lg(37507428, 96913792)).toEqual(lg(-2, -1))
      expect(lg(10, 0) % lg(-533680689, -79923599)).toEqual(lg(10, 0))
      expect(lg(-14, -1) % lg(-930313329, 2972085)).toEqual(lg(-14, -1))
      expect(lg(-20155233, -1) % lg(-49989774, -25498857)).toEqual(lg(
          -20155233, -1))
      expect(lg(-406, -1) % lg(2109762544, 126098611)).toEqual(lg(-406, -1))
      expect(lg(43, 0) % lg(598811771, 154269509)).toEqual(lg(43, 0))
      expect(lg(-4830, -1) % lg(-1043650540, -2874494)).toEqual(lg(-4830, -1))
      expect(lg(-4271, -1) % lg(-950378080, -106126516)).toEqual(lg(-4271, -1))
      expect(lg(126, 0) % lg(-877412093, -90804729)).toEqual(lg(126, 0))
      expect(lg(40445345, 0) % lg(-1461218790, 6749169)).toEqual(lg(40445345,
                                                                    0))
      expect(lg(-1, -1) % lg(1776909778, 28425796)).toEqual(lg(-1, -1))
      expect(lg(-2123811, -1) % lg(-51805125, 44153129)).toEqual(lg(-2123811,
                                                                    -1))
      expect(lg(-25650126, -1) % lg(-1317209725, -16141386)).toEqual(lg(
          -25650126, -1))
      expect(lg(30, 0) % lg(712479950, 158765535)).toEqual(lg(30, 0))
      expect(lg(2494211, 0) % lg(-432472367, 21859989)).toEqual(lg(2494211, 0))
      expect(lg(100937174, 0) % lg(212873269, -74778594)).toEqual(lg(100937174,
                                                                     0))
      expect(lg(901687, 0) % lg(-1225225931, -512562107)).toEqual(lg(901687,
                                                                     0))
      expect(lg(-422854, -1) % lg(-1361503923, -98826041)).toEqual(lg(-422854,
                                                                      -1))
      expect(lg(2, 0) % lg(386622050, -9945722)).toEqual(lg(2, 0))
      expect(lg(-465211, -1) % lg(-418132599, -160175963)).toEqual(lg(-465211,
                                                                      -1))
      expect(lg(63, 0) % lg(-1330189832, 180061391)).toEqual(lg(63, 0))
      expect(lg(47, 0) % lg(1439978282, -16520554)).toEqual(lg(47, 0))
      expect(lg(233450563, 0) % lg(-328511972, 377539644)).toEqual(lg(
          233450563, 0))
      expect(lg(-134912, -1) % lg(1349244684, -12612862)).toEqual(lg(-134912,
                                                                     -1))
      expect(lg(-95441, -1) % lg(511120357, 16112596)).toEqual(lg(-95441, -1))
      expect(lg(-1160726496, -1) % lg(-913371934, -9441145)).toEqual(lg(
          -1160726496, -1))
      expect(lg(-502, -1) % lg(-1021329523, -377728463)).toEqual(lg(-502, -1))
      expect(lg(3313324, 0) % lg(-67454848, 442297818)).toEqual(lg(3313324, 0))
      expect(lg(-145, -1) % lg(-1010112762, 29724438)).toEqual(lg(-145, -1))
      expect(lg(-19091, -1) % lg(-1944488998, -173788926)).toEqual(lg(-19091,
                                                                      -1))
      expect(lg(-3331910, -1) % lg(2144172121, 73505274)).toEqual(lg(-3331910,
                                                                     -1))
      expect(lg(56622, 0) % lg(-1451372835, 5219178)).toEqual(lg(56622, 0))
      expect(lg(0, 0) % lg(556032035, 32471322)).toEqual(lg(0, 0))
      expect(lg(800, 0) % lg(-1649243607, 2299368)).toEqual(lg(800, 0))
      expect(lg(86949, 0) % lg(794150820, -1384562176)).toEqual(lg(86949, 0))
      expect(lg(10, 0) % lg(-790693444, 1000869239)).toEqual(lg(10, 0))
      expect(lg(-333236, -1) % lg(-1020207444, 125043716)).toEqual(lg(-333236,
                                                                      -1))
      expect(lg(-598, -1) % lg(-93061561, -329975227)).toEqual(lg(-598, -1))
      expect(lg(-19, -1) % lg(-1096862531, 163621631)).toEqual(lg(-19, -1))
      expect(lg(465328283, 0) % lg(-21925149, -52057346)).toEqual(lg(465328283,
                                                                     0))
      expect(lg(-25837, -1) % lg(677002620, 8643698)).toEqual(lg(-25837, -1))
      expect(lg(-383633650, -1) % lg(1609519787, 8262009)).toEqual(lg(
          -383633650, -1))
      expect(lg(-66, -1) % lg(1917139359, 239618524)).toEqual(lg(-66, -1))
      expect(lg(1676620, 0) % lg(910745834, 82765572)).toEqual(lg(1676620, 0))

      // int53 / int32
      expect(lg(1244623439, 3) % lg(-231372097, -1)).toEqual(lg(15827410, 0))
      expect(lg(-1392787378, 124) % lg(-20252, -1)).toEqual(lg(15118, 0))
      expect(lg(578165055, 72) % lg(13, 0)).toEqual(lg(11, 0))
      expect(lg(-1836745385, 3) % lg(-95630157, -1)).toEqual(lg(42298679, 0))
      expect(lg(-1766124150, 29) % lg(-45315780, -1)).toEqual(lg(17447610, 0))
      expect(lg(540281958, 253606) % lg(-11, -1)).toEqual(lg(0, 0))
      expect(lg(-442404110, 7696) % lg(1489246, 0)).toEqual(lg(51980, 0))
      expect(lg(-631827526, 1455) % lg(8, 0)).toEqual(lg(2, 0))
      expect(lg(1266390909, 49) % lg(-34627848, -1)).toEqual(lg(5125741, 0))
      expect(lg(-453014259, 21413) % lg(149449, 0)).toEqual(lg(77691, 0))
      expect(lg(1573062436, 653) % lg(671211684, 0)).toEqual(lg(521867604, 0))
      expect(lg(-21113520, 0) % lg(177469767, 0)).toEqual(lg(14579368, 0))
      expect(lg(-262825676, 31) % lg(1, 0)).toEqual(lg(0, 0))
      expect(lg(-163968426, 1) % lg(33341027, 0)).toEqual(lg(24027362, 0))
      expect(lg(668741217, 14380) % lg(-11334498, -1)).toEqual(lg(6792805, 0))
      expect(lg(808041281, 1818) % lg(-10, -1)).toEqual(lg(9, 0))
      expect(lg(-1601247507, 25) % lg(-235, -1)).toEqual(lg(204, 0))
      expect(lg(-1577206289, 0) % lg(1618642, 0)).toEqual(lg(61089, 0))
      expect(lg(863396135, 503) % lg(-321808286, -1)).toEqual(lg(289305533, 0))
      expect(lg(-900149281, 55) % lg(15166197, 0)).toEqual(lg(7272892, 0))
      expect(lg(1802954050, 3593) % lg(7, 0)).toEqual(lg(3, 0))
      expect(lg(800669146, 41901) % lg(-20591, -1)).toEqual(lg(12036, 0))
      expect(lg(-1055636867, 39) % lg(48, 0)).toEqual(lg(29, 0))
      expect(lg(-491067123, 14) % lg(1, 0)).toEqual(lg(0, 0))
      expect(lg(1420289126, 67) % lg(1010219079, 0)).toEqual(lg(260441364, 0))
      expect(lg(1338756461, 32) % lg(-4427443, -1)).toEqual(lg(3936541, 0))
      expect(lg(-820843233, 778) % lg(-273780418, -1)).toEqual(lg(183313645,
                                                                  0))
      expect(lg(-1033566360, 561225) % lg(-156677, -1)).toEqual(lg(91783, 0))
      expect(lg(-1567070603, 38) % lg(-8, -1)).toEqual(lg(5, 0))
      expect(lg(-1649343541, 185302) % lg(-19368267, -1)).toEqual(lg(11214823,
                                                                     0))
      expect(lg(-591434325, 76351) % lg(94212, 0)).toEqual(lg(75719, 0))
      expect(lg(235794528, 55) % lg(17599, 0)).toEqual(lg(10941, 0))
      expect(lg(-763589741, 116) % lg(-14942, -1)).toEqual(lg(5331, 0))
      expect(lg(-1283158225, 237055) % lg(-2, -1)).toEqual(lg(1, 0))
      expect(lg(1537105400, 29108) % lg(-37848, -1)).toEqual(lg(24400, 0))
      expect(lg(-56778611, 994650) % lg(-170, -1)).toEqual(lg(95, 0))
      expect(lg(-2057746932, 7) % lg(-10100, -1)).toEqual(lg(9836, 0))
      expect(lg(1365793356, 12) % lg(-38454651, -1)).toEqual(lg(30255783, 0))
      expect(lg(-2128793438, 4) % lg(6825, 0)).toEqual(lg(417, 0))
      expect(lg(1667515072, 8) % lg(2, 0)).toEqual(lg(0, 0))
      expect(lg(420324337, 980) % lg(-845, -1)).toEqual(lg(257, 0))
      expect(lg(-771084081, 8204) % lg(105392, 0)).toEqual(lg(82991, 0))
      expect(lg(-332377894, 1) % lg(882238, 0)).toEqual(lg(691256, 0))
      expect(lg(1749263284, 11) % lg(-20, -1)).toEqual(lg(0, 0))
      expect(lg(347303218, 1234317) % lg(-13, -1)).toEqual(lg(4, 0))
      expect(lg(1199079324, 17271) % lg(11033, 0)).toEqual(lg(150, 0))
      expect(lg(1196217208, 13) % lg(-23, -1)).toEqual(lg(14, 0))
      expect(lg(-1078128939, 0) % lg(740155481, 0)).toEqual(lg(256216433, 0))
      expect(lg(-1354463473, 3691) % lg(-63588, -1)).toEqual(lg(45583, 0))
      expect(lg(-1255896801, 1469630) % lg(-502, -1)).toEqual(lg(459, 0))

      // int53 % int53
      expect(lg(1805177178, 1) % lg(-1293833696, 410)).toEqual(lg(1805177178,
                                                                  1))
      expect(lg(647007072, 1811985) % lg(1091239449, 3)).toEqual(lg(-583440651,
                                                                    2))
      expect(lg(1346307032, 1) % lg(-672335266, 33)).toEqual(lg(1346307032, 1))
      expect(lg(858355422, 81) % lg(1490435172, 162402)).toEqual(lg(858355422,
                                                                    81))
      expect(lg(-1299053281, 6330) % lg(1042770708, 1)).toEqual(lg(744276027,
                                                                   1))
      expect(lg(-88774269, 25) % lg(775537355, 1)).toEqual(lg(29273105, 0))
      expect(lg(-962613261, 4309) % lg(-529185362, 5)).toEqual(lg(383200445,
                                                                  2))
      expect(lg(-171009725, 445) % lg(-1167557775, 307982)).toEqual(lg(
          -171009725, 445))
      expect(lg(1848497503, 78519) % lg(1533824479, 15755)).toEqual(lg(8166883,
                                                                       15498))
      expect(lg(-1752533311, 17) % lg(1904799096, 73566)).toEqual(lg(
          -1752533311, 17))
      expect(lg(-1641266817, 46) % lg(-31936789, 751199)).toEqual(lg(
          -1641266817, 46))
      expect(lg(-637954451, 32352) % lg(-10259599, 1131)).toEqual(lg(
          -350685679, 656))
      expect(lg(-1657673170, 122149) % lg(-534342412, 0)).toEqual(lg(
          -1671876486, 0))
      expect(lg(-660565679, 235) % lg(-897090894, 14655)).toEqual(lg(
          -660565679, 235))
      expect(lg(-1798560222, 612) % lg(-236039758, 2924)).toEqual(lg(
          -1798560222, 612))
      expect(lg(1010899296, 62798) % lg(-1974205776, 9515)).toEqual(lg(
          -28767936, 5704))
      expect(lg(1206965517, 91420) % lg(880030876, 7)).toEqual(lg(-2004786867,
                                                                  4))
      expect(lg(712148070, 3) % lg(472319826, 2838)).toEqual(lg(712148070, 3))
      expect(lg(-1275175525, 44) % lg(162799342, 861329)).toEqual(lg(
          -1275175525, 44))
      expect(lg(-516916094, 191396) % lg(-1920802608, 30)).toEqual(lg(
          1187224322, 14))
      expect(lg(-1627551726, 4499) % lg(1200735793, 1)).toEqual(lg(-1461747946,
                                                                   0))
      expect(lg(453535447, 39039) % lg(520791957, 141909)).toEqual(lg(
          453535447, 39039))
      expect(lg(216221627, 20) % lg(-781572865, 8131)).toEqual(lg(216221627,
                                                                  20))
      expect(lg(-1999221053, 528) % lg(1107934896, 25)).toEqual(lg(1611884803,
                                                                   23))
      expect(lg(-701225584, 44) % lg(-1403297482, 0)).toEqual(lg(1722095012,
                                                                 0))
      expect(lg(-232837834, 5049) % lg(1000581509, 15836)).toEqual(lg(
          -232837834, 5049))
      expect(lg(-82376749, 239) % lg(-163409376, 7688)).toEqual(lg(-82376749,
                                                                   239))
      expect(lg(941363778, 110) % lg(336092572, 3)).toEqual(lg(2063025646, 2))
      expect(lg(1004884706, 1133) % lg(283309861, 750)).toEqual(lg(721574845,
                                                                   383))
      expect(lg(1436404594, 1595) % lg(1522987410, 70)).toEqual(lg(-2004547354,
                                                                   47))
      expect(lg(1696970595, 8) % lg(-1168832286, 4163)).toEqual(lg(1696970595,
                                                                   8))
      expect(lg(-1244970780, 32) % lg(394179266, 13)).toEqual(lg(-2033329312,
                                                                 6))
      expect(lg(1864629418, 1) % lg(528888491, 970677)).toEqual(lg(1864629418,
                                                                   1))
      expect(lg(-1763600443, 962032) % lg(1535552275, 102108)).toEqual(lg(
          1596298266, 43057))
      expect(lg(1181714932, 5) % lg(1296434411, 26359)).toEqual(lg(1181714932,
                                                                   5))
      expect(lg(1535735456, 276446) % lg(-1930593680, 7)).toEqual(lg(
          -2140209952, 7))
      expect(lg(2079501385, 97596) % lg(-1803771626, 21)).toEqual(lg(
          -1703068243, 11))
      expect(lg(286993796, 174379) % lg(656426284, 70488)).toEqual(lg(
          -1025858772, 33402))
      expect(lg(221015334, 1635766) % lg(-2014306775, 270673)).toEqual(lg(
          -578045904, 11724))
      expect(lg(-2103734262, 977) % lg(-22949494, 920)).toEqual(lg(-2080784768,
                                                                   56))
      expect(lg(-922083739, 29) % lg(2040148267, 19160)).toEqual(lg(-922083739,
                                                                    29))
      expect(lg(-559850131, 11989) % lg(1366001936, 2880)).toEqual(lg(
          -1728890579, 468))
      expect(lg(-1071198220, 2182) % lg(1526886260, 17)).toEqual(lg(1341547600,
                                                                    13))
      expect(lg(-896451936, 45) % lg(2132477227, 164356)).toEqual(lg(
          -896451936, 45))
      expect(lg(-561327714, 1420) % lg(-368698210, 151)).toEqual(lg(
          -1538011120, 53))
      expect(lg(2112956103, 118429) % lg(-374507565, 859)).toEqual(lg(
          1880884956, 621))
      expect(lg(380445410, 8) % lg(-1822479769, 1)).toEqual(lg(902909663, 0))
      expect(lg(-1867274924, 105813) % lg(175641312, 79)).toEqual(lg(
          -652149100, 56))
      expect(lg(-991170416, 37) % lg(1740161397, 88122)).toEqual(lg(-991170416,
                                                                    37))
      expect(lg(-31602776, 1) % lg(-503633567, 241909)).toEqual(lg(-31602776,
                                                                   1))

      // int53 % big
      expect(lg(-930109303, 3) % lg(1606982787, 925386547)).toEqual(lg(
          -930109303, 3))
      expect(lg(-717668907, 16251) % lg(2079100937, 7825426)).toEqual(lg(
          -717668907, 16251))
      expect(lg(265990345, 3) % lg(-1140922127, -3108870)).toEqual(lg(
          265990345, 3))
      expect(lg(-1181318422, 1) % lg(1489652251, 75207246)).toEqual(lg(
          -1181318422, 1))
      expect(lg(380276439, 59) % lg(-1062351234, -3631372)).toEqual(lg(
          380276439, 59))
      expect(lg(1080382784, 7211) % lg(572850722, -139092025)).toEqual(lg(
          1080382784, 7211))
      expect(lg(2020323378, 316) % lg(1716930349, -16333391)).toEqual(lg(
          2020323378, 316))
      expect(lg(1302118364, 5) % lg(-442067036, 1941456592)).toEqual(lg(
          1302118364, 5))
      expect(lg(-641137972, 602) % lg(1134212295, -135713760)).toEqual(lg(
          -641137972, 602))
      expect(lg(-761172703, 499) % lg(769981236, 12756336)).toEqual(lg(
          -761172703, 499))
      expect(lg(1601268090, 610) % lg(448513898, -160887452)).toEqual(lg(
          1601268090, 610))
      expect(lg(-16483553, 0) % lg(-1253549192, -1748027086)).toEqual(lg(
          -16483553, 0))
      expect(lg(-1284021361, 241) % lg(13275221, -3818882)).toEqual(lg(
          -1284021361, 241))
      expect(lg(1499414278, 26) % lg(570654893, -17498947)).toEqual(lg(
          1499414278, 26))
      expect(lg(-368610421, 5074) % lg(685701351, 31070898)).toEqual(lg(
          -368610421, 5074))
      expect(lg(1200134796, 70) % lg(1230376618, -2490370)).toEqual(lg(
          1200134796, 70))
      expect(lg(1537764087, 64483) % lg(-1252591472, 66761881)).toEqual(lg(
          1537764087, 64483))
      expect(lg(-1981129198, 15) % lg(1937978150, 8201544)).toEqual(lg(
          -1981129198, 15))
      expect(lg(32422964, 200) % lg(2051327691, -20319622)).toEqual(lg(
          32422964, 200))
      expect(lg(1404616230, 30) % lg(-748420073, -120320053)).toEqual(lg(
          1404616230, 30))
      expect(lg(-1860381107, 38) % lg(392948122, 60098039)).toEqual(lg(
          -1860381107, 38))
      expect(lg(1050519262, 106431) % lg(361773491, -6329760)).toEqual(lg(
          1050519262, 106431))
      expect(lg(460136491, 1681770) % lg(1399049044, 759923035)).toEqual(lg(
          460136491, 1681770))
      expect(lg(2065599344, 11089) % lg(-465681057, 3484544)).toEqual(lg(
          2065599344, 11089))
      expect(lg(1849358428, 418531) % lg(1023666326, 3435570)).toEqual(lg(
          1849358428, 418531))
      expect(lg(1292603836, 80) % lg(-1114872574, 250120091)).toEqual(lg(
          1292603836, 80))
      expect(lg(1456627133, 194844) % lg(-1256385160, 59427917)).toEqual(lg(
          1456627133, 194844))
      expect(lg(-568179858, 160) % lg(1142846538, 154324747)).toEqual(lg(
          -568179858, 160))
      expect(lg(-2133580755, 203337) % lg(111334842, 12695612)).toEqual(lg(
          -2133580755, 203337))
      expect(lg(1961218705, 6687) % lg(-245612957, 134017780)).toEqual(lg(
          1961218705, 6687))
      expect(lg(335350966, 55096) % lg(-1815119598, -120983980)).toEqual(lg(
          335350966, 55096))
      expect(lg(-767561503, 211) % lg(554589640, -7873602)).toEqual(lg(
          -767561503, 211))
      expect(lg(1476687067, 3767) % lg(552659809, -753378142)).toEqual(lg(
          1476687067, 3767))
      expect(lg(-1107393223, 30) % lg(-78383575, -52663801)).toEqual(lg(
          -1107393223, 30))
      expect(lg(607313614, 2) % lg(-234099925, 59184919)).toEqual(lg(607313614,
                                                                     2))
      expect(lg(-1542671184, 616882) % lg(1370026838, -45628731)).toEqual(lg(
          -1542671184, 616882))
      expect(lg(525616384, 1001) % lg(1995646126, -11226360)).toEqual(lg(
          525616384, 1001))
      expect(lg(2109958916, 21549) % lg(-419960245, -115959896)).toEqual(lg(
          2109958916, 21549))
      expect(lg(-450913111, 32140) % lg(-99267096, -3640047)).toEqual(lg(
          -450913111, 32140))
      expect(lg(1515870052, 198) % lg(1415757861, -110282301)).toEqual(lg(
          1515870052, 198))
      expect(lg(124639649, 865615) % lg(-1354782388, 2569606)).toEqual(lg(
          124639649, 865615))
      expect(lg(557119825, 7205) % lg(683150209, -15864187)).toEqual(lg(
          557119825, 7205))
      expect(lg(992846513, 1385110) % lg(1578961851, -8380578)).toEqual(lg(
          992846513, 1385110))
      expect(lg(1081385155, 4176) % lg(1892231070, 31130825)).toEqual(lg(
          1081385155, 4176))
      expect(lg(-738492748, 8) % lg(-431212066, 687916944)).toEqual(lg(
          -738492748, 8))
      expect(lg(-1448153936, 8101) % lg(-584523654, -4814205)).toEqual(lg(
          -1448153936, 8101))
      expect(lg(-713251055, 243) % lg(261411225, 31444708)).toEqual(lg(
          -713251055, 243))
      expect(lg(881178812, 47057) % lg(823893049, -5940358)).toEqual(lg(
          881178812, 47057))
      expect(lg(-506817388, 0) % lg(-465610822, 10559551)).toEqual(lg(
          -506817388, 0))
      expect(lg(-420315839, 112832) % lg(-686319219, -666166549)).toEqual(lg(
          -420315839, 112832))

      // big % int32
      expect(lg(-412174169, -319069709) % lg(-6, -1)).toEqual(lg(-3, -1))
      expect(lg(1634601702, 814446468) % lg(825883, 0)).toEqual(lg(464005, 0))
      expect(lg(-1005992901, 2694218) % lg(108493743, 0)).toEqual(lg(34559370,
                                                                     0))
      expect(lg(1534700309, -630528658) % lg(-506616, -1)).toEqual(lg(-286379,
                                                                      -1))
      expect(lg(-456613426, -23298167) % lg(-206, -1)).toEqual(lg(-62, -1))
      expect(lg(857770611, 2618490) % lg(1225551197, 0)).toEqual(lg(386945695,
                                                                    0))
      expect(lg(2127943654, 2768088) % lg(-291653, -1)).toEqual(lg(270232, 0))
      expect(lg(1085973072, 3470797) % lg(-29714535, -1)).toEqual(lg(277129,
                                                                     0))
      expect(lg(1536124828, 1268901218) % lg(-121, -1)).toEqual(lg(15, 0))
      expect(lg(371220141, 34588968) % lg(2, 0)).toEqual(lg(1, 0))
      expect(lg(-1712997009, 187259899) % lg(129274, 0)).toEqual(lg(46669, 0))
      expect(lg(586579000, -243530833) % lg(-31235, -1)).toEqual(lg(-1508, -1))
      expect(lg(1745775262, -400161972) % lg(-1, -1)).toEqual(lg(0, 0))
      expect(lg(-1564631310, -56487209) % lg(2626, 0)).toEqual(lg(-1680, -1))
      expect(lg(-1848745069, 11533547) % lg(59, 0)).toEqual(lg(53, 0))
      expect(lg(-1415791920, -26215621) % lg(-2142359, -1)).toEqual(lg(
          -1699972, -1))
      expect(lg(-481609933, -25891343) % lg(483607, 0)).toEqual(lg(-200041,
                                                                   -1))
      expect(lg(-889674017, -4084771) % lg(428648085, 0)).toEqual(lg(-13123232,
                                                                     -1))
      expect(lg(1587465684, -367383975) % lg(7, 0)).toEqual(lg(0, 0))
      expect(lg(811562260, -335104547) % lg(5502, 0)).toEqual(lg(-4528, -1))
      expect(lg(2107357891, -10075787) % lg(110, 0)).toEqual(lg(-71, -1))
      expect(lg(-1356326655, 5174156) % lg(-1, -1)).toEqual(lg(0, 0))
      expect(lg(-1794856776, 3059124) % lg(-29413816, -1)).toEqual(lg(7872112,
                                                                      0))
      expect(lg(-1118254374, -3629384) % lg(-85, -1)).toEqual(lg(-37, -1))
      expect(lg(288539563, 70814306) % lg(-14561, -1)).toEqual(lg(14227, 0))
      expect(lg(-719069745, -128562664) % lg(-256, -1)).toEqual(lg(-49, -1))
      expect(lg(1530955727, 15829469) % lg(195494, 0)).toEqual(lg(6101, 0))
      expect(lg(2144004402, -5408490) % lg(11, 0)).toEqual(lg(-6, -1))
      expect(lg(-1766192560, -17443468) % lg(-168087095, -1)).toEqual(lg(
          -137624717, -1))
      expect(lg(-524619138, -371121095) % lg(4765, 0)).toEqual(lg(-3592, -1))
      expect(lg(-1960083221, 176122524) % lg(-5564, -1)).toEqual(lg(4335, 0))
      expect(lg(1528631102, -597885631) % lg(-413908, -1)).toEqual(lg(-271754,
                                                                      -1))
      expect(lg(-1513123614, -30582360) % lg(-496311, -1)).toEqual(lg(-361112,
                                                                      -1))
      expect(lg(-1975522255, -46421733) % lg(29, 0)).toEqual(lg(-4, -1))
      expect(lg(-1715879325, 3072313) % lg(438221, 0)).toEqual(lg(414436, 0))
      expect(lg(-1321015849, -300384564) % lg(1, 0)).toEqual(lg(0, 0))
      expect(lg(-1088390706, -277354665) % lg(-1237, -1)).toEqual(lg(-454, -1))
      expect(lg(-1012773943, 223943652) % lg(707359548, 0)).toEqual(lg(
          586891857, 0))
      expect(lg(1097288344, 26740237) % lg(-3, -1)).toEqual(lg(2, 0))
      expect(lg(-1121404205, -87484234) % lg(80229261, 0)).toEqual(lg(
          -24053960, -1))
      expect(lg(-1503637931, -163703901) % lg(-983334452, -1)).toEqual(lg(
          -79944815, -1))
      expect(lg(2012820970, 445991475) % lg(1035472980, 0)).toEqual(lg(2600110,
                                                                       0))
      expect(lg(2015362538, 2985510) % lg(-148, -1)).toEqual(lg(74, 0))
      expect(lg(1764134228, 50881407) % lg(-1, -1)).toEqual(lg(0, 0))
      expect(lg(-523555853, 77167937) % lg(-563, -1)).toEqual(lg(106, 0))
      expect(lg(1531888651, -2389306) % lg(1, 0)).toEqual(lg(0, 0))
      expect(lg(-181277952, 32599207) % lg(-729, -1)).toEqual(lg(659, 0))
      expect(lg(223126732, 88838488) % lg(13378, 0)).toEqual(lg(968, 0))
      expect(lg(670834629, 46037187) % lg(922370, 0)).toEqual(lg(920991, 0))
      expect(lg(1098978850, 6541822) % lg(-8405198, -1)).toEqual(lg(2462152,
                                                                    0))

      // big % int53
      expect(lg(2008672965, 41566313) % lg(313991275, 18390)).toEqual(lg(
          1057995305, 4748))
      expect(lg(1922552561, 28139870) % lg(-2083633557, 19)).toEqual(lg(
          -1074209653, 18))
      expect(lg(843627074, -173776705) % lg(1451117493, 14364)).toEqual(lg(
          1480601143, -11310))
      expect(lg(204865470, -6692402) % lg(-645190286, 413)).toEqual(lg(
          -691687452, -38))
      expect(lg(952830559, -214594684) % lg(-1778162360, 378)).toEqual(lg(
          -1218791457, -31))
      expect(lg(1673740333, -69274846) % lg(-1549261605, 2390)).toEqual(lg(
          -281609960, -1292))
      expect(lg(-1276804811, 367022678) % lg(-678111623, 11)).toEqual(lg(
          -860426348, 1))
      expect(lg(-1331527548, -33013551) % lg(-1975438267, 2961)).toEqual(lg(
          -1244563205, -1264))
      expect(lg(1067523314, 72606174) % lg(-1716982106, 255179)).toEqual(lg(
          -935830326, 135167))
      expect(lg(-937134490, -32649070) % lg(-804857990, 57507)).toEqual(lg(
          -2025081444, -42140))
      expect(lg(108363299, 1224097478) % lg(1137551776, 281)).toEqual(lg(
          85696931, 194))
      expect(lg(-1965834834, -11053948) % lg(-942300324, 6487)).toEqual(lg(
          -385517902, -5258))
      expect(lg(-3151939, 171473802) % lg(-2071379940, 3914)).toEqual(lg(
          -755355475, 2268))
      expect(lg(1465781759, -970108425) % lg(-1251607207, 3003)).toEqual(lg(
          -676865399, -663))
      expect(lg(919308511, -1689158617) % lg(658566728, 36406)).toEqual(lg(
          2042443783, -22321))
      expect(lg(-418485001, 1000432592) % lg(-1653953022, 31957)).toEqual(lg(
          -903837593, 31415))
      expect(lg(-880302655, -14116770) % lg(913871933, 118223)).toEqual(lg(
          496274972, -48207))
      expect(lg(-525597278, -3790314) % lg(2133284776, 127083)).toEqual(lg(
          1210119082, -104892))
      expect(lg(-393124913, -28106221) % lg(958070140, 159)).toEqual(lg(
          473810731, -5))
      expect(lg(6929245, 2749730) % lg(1462129294, 43237)).toEqual(lg(
          -1912903061, 25777))
      expect(lg(708024745, -15568245) % lg(1288198049, 56)).toEqual(lg(
          1099532724, -19))
      expect(lg(487601139, 13603229) % lg(723875593, 45021)).toEqual(lg(
          920504149, 6836))
      expect(lg(-2070321133, 115478389) % lg(-1799479616, 75)).toEqual(lg(
          1778080723, 29))
      expect(lg(-307180735, 3049800) % lg(1043781053, 3319)).toEqual(lg(
          -720480381, 2735))
      expect(lg(-1073877839, -6538577) % lg(-1408649838, 0)).toEqual(lg(
          1473972065, -1))
      expect(lg(-1892822171, -1698321438) % lg(96164237, 514)).toEqual(lg(
          -1389255096, -200))
      expect(lg(-674980011, 2764943) % lg(-445529419, 65125)).toEqual(lg(
          857386403, 29656))
      expect(lg(2003347800, -46928389) % lg(368897711, 128159)).toEqual(lg(
          -419043446, -22164))
      expect(lg(-1929871429, -241628283) % lg(202358381, 7645)).toEqual(lg(
          -1599543668, -6569))
      expect(lg(419719197, 661188517) % lg(2112360098, 1)).toEqual(lg(
          581185953, 1))
      expect(lg(1092830824, 1600823129) % lg(-1827462760, 172800)).toEqual(lg(
          -1880704128, 171407))
      expect(lg(-836779994, -27475595) % lg(-417527207, 16)).toEqual(lg(
          1210159480, -13))
      expect(lg(-1759597755, 9157722) % lg(-987185779, 1)).toEqual(lg(
          807846066, 1))
      expect(lg(-1097231525, 20092165) % lg(1106421078, 1)).toEqual(lg(
          949995673, 1))
      expect(lg(390678483, 3835040) % lg(1221250555, 14)).toEqual(lg(
          -712450167, 7))
      expect(lg(-284334384, -18425278) % lg(-1111448031, 6)).toEqual(lg(
          1129531033, -4))
      expect(lg(-233961390, 53260849) % lg(-613558136, 3663)).toEqual(lg(
          2094997010, 3022))
      expect(lg(-3383211, 8039036) % lg(-1668680584, 749874)).toEqual(lg(
          -496446555, 540290))
      expect(lg(804358887, -189240235) % lg(179665302, 12347)).toEqual(lg(
          1280740603, -9472))
      expect(lg(208769744, 280071599) % lg(-325433064, 14)).toEqual(lg(
          2127427912, 6))
      expect(lg(-1527711901, -51564742) % lg(-1019145455, 0)).toEqual(lg(
          -722136158, -1))
      expect(lg(-159182038, -2145592347) % lg(-483720705, 15)).toEqual(lg(
          -1603688570, -2))
      expect(lg(1059926378, 477886379) % lg(924988992, 543468)).toEqual(lg(
          -256578646, 177817))
      expect(lg(-1800046387, 119696078) % lg(436524799, 94037)).toEqual(lg(
          1286157765, 80885))
      expect(lg(-822280387, 44882065) % lg(-940828508, 22947)).toEqual(lg(
          251450065, 19154))
      expect(lg(1465101985, 269803551) % lg(-1953360551, 334)).toEqual(lg(
          1310986115, 209))
      expect(lg(-567675197, -8838663) % lg(1903221047, 6)).toEqual(lg(
          1436855439, -5))
      expect(lg(689376065, -22622471) % lg(1534988921, 63)).toEqual(lg(
          296887390, -17))
      expect(lg(-2017356377, -57717216) % lg(-1390284125, 42)).toEqual(lg(
          1577958450, -39))
      expect(lg(-128715878, 982583003) % lg(2004099318, 988167)).toEqual(lg(
          661387374, 344542))

      // big % big
      expect(lg(-320078007, 205603273) % lg(2020227799, -360928021))
        .toEqual(lg(-320078007, 205603273))
      expect(lg(-800732960, -371808530) % lg(744251542, -11199592)).toEqual(lg(
          408769930, -2221999))
      expect(lg(-56774921, -32434115) % lg(1413374280, -2726592)).toEqual(lg(
          1575977183, -2441606))
      expect(lg(1667937500, 228622683) % lg(-243248020, 69909529)).toEqual(lg(
          -1897285736, 18894093))
      expect(lg(-1333815518, 2097776) % lg(-1750106076, 18608702)).toEqual(lg(
          -1333815518, 2097776))
      expect(lg(-162800691, -117885498) % lg(-709007774, 8711127)).toEqual(lg(
          -789967161, -4640836))
      expect(lg(-1909427145, -2824029) % lg(2028036056, -660713154))
        .toEqual(lg(-1909427145, -2824029))
      expect(lg(14077923, 63046905) % lg(-688765214, 375445962)).toEqual(lg(
          14077923, 63046905))
      expect(lg(272760540, 19525127) % lg(-396955631, 848435537)).toEqual(lg(
          272760540, 19525127))
      expect(lg(-600396362, 406643261) % lg(-1533973181, 491661310))
        .toEqual(lg(-600396362, 406643261))
      expect(lg(1801834226, 200420454) % lg(-1889418050, -328758068))
        .toEqual(lg(1801834226, 200420454))
      expect(lg(1170836790, 510289402) % lg(202445942, 113936327)).toEqual(lg(
          361053022, 54544094))
      expect(lg(-378923036, -1036580478) % lg(905093048, 5526353)).toEqual(lg(
          1369752396, -3152427))
      expect(lg(-2137034353, 1455139814) % lg(1665353214, 27574343))
        .toEqual(lg(1458911735, 21273958))
      expect(lg(-1350216191, -3821167) % lg(-1333339390, -4746360)).toEqual(lg(
          -1350216191, -3821167))
      expect(lg(-1289646201, -5193401) % lg(1838778646, -3822651)).toEqual(lg(
          1166542449, -1370750))
      expect(lg(301867174, 5185218) % lg(157012848, -15464466)).toEqual(lg(
          301867174, 5185218))
      expect(lg(467711834, 155069651) % lg(-44860799, 106733768)).toEqual(lg(
          512572633, 48335882))
      expect(lg(1624269582, 11007763) % lg(-158694824, -491219717)).toEqual(lg(
          1624269582, 11007763))
      expect(lg(-1015519521, -163989350) % lg(1652525166, 530116116))
        .toEqual(lg(-1015519521, -163989350))
      expect(lg(2001612518, -452587333) % lg(1115217917, 90680733)).toEqual(lg(
          -2127450406, -89864400))
      expect(lg(1039524645, -86121932) % lg(1131434363, 13339357)).toEqual(lg(
          -761803769, -6085789))
      expect(lg(-1922291990, 6439098) % lg(-1083372307, -20634200)).toEqual(lg(
          -1922291990, 6439098))
      expect(lg(1408756974, 235847122) % lg(-1813277898, -9066180)).toEqual(lg(
          1508171882, 126457))
      expect(lg(1121009342, -1533788016) % lg(-1724900447, -5821788))
        .toEqual(lg(-496706473, -2657930))
      expect(lg(-1626361260, -113469353) % lg(1216987736, -817139415))
        .toEqual(lg(-1626361260, -113469353))
      expect(lg(-433139577, -182483493) % lg(1019490766, -595625160))
        .toEqual(lg(-433139577, -182483493))
      expect(lg(793542905, 198273616) % lg(-82759497, -2621599)).toEqual(lg(
          -1118452074, 1653764))
      expect(lg(425605214, 249789222) % lg(392156278, 6716943)).toEqual(lg(
          -1199275184, 1262327))
      expect(lg(213473729, 11660532) % lg(-547058106, 894811834)).toEqual(lg(
          213473729, 11660532))
      expect(lg(-1550227391, 2847368) % lg(-1996700003, 689370771)).toEqual(lg(
          -1550227391, 2847368))
      expect(lg(-144234222, -54239417) % lg(-1102770075, -7213193)).toEqual(lg(
          -1014778289, -3747071))
      expect(lg(524484467, 15124083) % lg(-1101379967, -39968226)).toEqual(lg(
          524484467, 15124083))
      expect(lg(314758022, 5390195) % lg(-1234755328, -3305123)).toEqual(lg(
          -919997306, 2085072))
      expect(lg(580679232, -10426812) % lg(-1964013803, -1738507605))
        .toEqual(lg(580679232, -10426812))
      expect(lg(1670083752, -254253193) % lg(722212413, -125031969))
        .toEqual(lg(225658926, -4189255))
      expect(lg(-1744001445, -5443198) % lg(1248252191, 3609991)).toEqual(lg(
          -495749254, -1833207))
      expect(lg(-1786439869, 137339199) % lg(1821158508, 2909161)).toEqual(lg(
          -1481543825, 608612))
      expect(lg(1273422584, -284542935) % lg(1626032463, -17392208))
        .toEqual(lg(1026706952, -6267613))
      expect(lg(-513801887, -32580141) % lg(-342074286, 27651829)).toEqual(lg(
          -855876173, -4928311))
      expect(lg(-1027906958, 55543678) % lg(-1936394792, 928937151))
        .toEqual(lg(-1027906958, 55543678))
      expect(lg(251585986, -50474191) % lg(-2045396991, 32687162)).toEqual(lg(
          -1793811005, -17787029))
      expect(lg(66679938, -917589429) % lg(2124767660, -3454168)).toEqual(lg(
          -356034186, -2235041))
      expect(lg(-599564184, -209788131) % lg(-325046915, 133280284))
        .toEqual(lg(-924611099, -76507846))
      expect(lg(838338995, -12983151) % lg(-842402530, 19411056)).toEqual(lg(
          838338995, -12983151))
      expect(lg(1444498155, 520850879) % lg(851271837, 23920116)).toEqual(lg(
          747658762, 18528439))
      expect(lg(2096765386, -117024114) % lg(-1726450785, -5694999))
        .toEqual(lg(-2028924578, -3124146))
      expect(lg(-425905039, -180148939) % lg(-1397064581, -15926795))
        .toEqual(lg(2056903464, -4954201))
      expect(lg(-920215872, 219325473) % lg(1357686103, 54682263)).toEqual(lg(
          -2055992988, 596420))
      expect(lg(1279110660, -10784541) % lg(278869448, 758126792)).toEqual(lg(
          1279110660, -10784541))
    }
  }
}

object RuntimeLongOldTest extends JasmineTest {
  import RuntimeLong.fromDouble

  /** overload expect for long to add toString */
  def expect(l: RuntimeLong): JasmineExpectation = expect(l.toHexString)

  describe("scala.scalajs.runtime.RuntimeLong - old") {

    // scalastyle:off disallow.space.before.token disallow.space.after.token

    def fromInt(x: Int): RuntimeLong = new RuntimeLong(x)

    val maxInt = fromInt(Int.MaxValue)
    val minInt = fromInt(Int.MinValue)
    val one = fromInt(1)
    val billion = fromInt(1000000000)

    val `4503599627370510L` = new RuntimeLong(14, 0, 256)
    val `613354684553L` = new RuntimeLong(639113, 146235, 0)
    val `9863155567412L` = new RuntimeLong(2247476, 2351559, 0)
    val `3632147899696541255L` = new RuntimeLong(1568327, 2954580, 206463)
    val `7632147899696541255L` = new RuntimeLong(2616903, 1593290, 433837)

    val minValue = new RuntimeLong(0, 0, 524288)
    val minus1 = new RuntimeLong(4194303, 4194303, 1048575)
    val minus2 = new RuntimeLong(4194302, 4194303, 1048575)
    val minus3 = new RuntimeLong(4194301, 4194303, 1048575)
    val minus4 = new RuntimeLong(4194300, 4194303, 1048575)
    val minus15 = new RuntimeLong(4194289, 4194303, 1048575)
    val minus16 = new RuntimeLong(4194288, 4194303, 1048575)

    it("should correctly implement negation") {
      expect(-fromInt(5)).toEqual("fffffffffffffffb")
      expect(-fromInt(0)).toEqual("0")
      expect(-minInt).toEqual("80000000")
    }

    it("should correctly implement comparison") {
      expect(fromInt(7) < fromInt(15)).toBe(true)
      expect(fromInt(15) < fromInt(15)).toBe(false)
      expect(fromInt(15) <= fromInt(15)).toBe(true)
      expect(fromInt(14) <= fromInt(15)).toBe(true)
      expect(fromInt(15) > fromInt(15)).toBe(false)
      expect(fromInt(14) > fromInt(15)).toBe(false)
      expect(fromInt(16) > fromInt(15)).toBe(true)
      expect(fromInt(15) >= fromInt(15)).toBe(true)
      expect(fromInt(14) >= fromInt(15)).toBe(false)
      expect(fromInt(16) >= fromInt(15)).toBe(true)
    }

    it("should correctly implement addition") {
      expect(fromInt(7) + fromInt(15)).toEqual("16")
      expect(maxInt + maxInt).toEqual("fffffffe")
      expect(maxInt + one).toEqual("80000000")
    }

    it("should correctly implement subtraction") {
      expect(fromInt(7) - fromInt(15)).toEqual("fffffffffffffff8")
      expect(maxInt - maxInt).toEqual("0")
    }

    it("should correctly implement multiplication") {
      expect(fromInt(7) * fromInt(15)).toEqual("69")
      expect(fromInt(-7) * fromInt(15)).toEqual("ffffffffffffff97")
      expect(maxInt * maxInt).toEqual("3fffffff00000001")
      expect(`4503599627370510L` * fromInt(-4)).toEqual("ffbfffffffffffc8")
    }

    it("should correctly implement division") {
      expect(fromInt(7) / fromInt(15)).toEqual("0")
      expect(fromInt(24) / fromInt(5)).toEqual("4")
      expect(fromInt(24) / fromInt(-5)).toEqual("fffffffffffffffc")
      expect(maxInt / fromInt(-5)).toEqual("ffffffffe6666667")
      expect(maxInt / billion).toEqual("2")
      expect((maxInt + one) / billion).toEqual("2")

      expect(minValue / minValue).toEqual("1")
      expect(minValue / minus1).toEqual("8000000000000000")
      expect(minValue / minus2).toEqual("4000000000000000")
      expect(minValue / minus3).toEqual("2aaaaaaaaaaaaaaa")
      expect(minValue / minus4).toEqual("2000000000000000")
      expect(minValue / minus15).toEqual("888888888888888")
      expect(minValue / minus16).toEqual("800000000000000")

      expect(`7632147899696541255L` / minValue).toEqual("0")
      expect(`7632147899696541255L` / minus1).toEqual("961529ec0d5811b9")
      expect(`7632147899696541255L` / minus2).toEqual("cb0a94f606ac08dd")
      expect(`7632147899696541255L` / minus3).toEqual("dcb1b8a40472b093")
      expect(`7632147899696541255L` / minus4).toEqual("e5854a7b0356046f")
      expect(`7632147899696541255L` / minus15).toEqual("f8f05820cdb089b7")
      expect(`7632147899696541255L` / minus16).toEqual("f961529ec0d5811c")
    }

    it("should correctly implement modulus") {
      expect(fromInt(7) % fromInt(15)).toEqual("7")
      expect(fromInt(24) % fromInt(5)).toEqual("4")
      expect(fromInt(24) % fromInt(-5)).toEqual("4")
      expect(maxInt % billion).toEqual("8ca6bff")
      expect((maxInt + one) % billion).toEqual("8ca6c00")
      expect(maxInt % fromInt(-5)).toEqual("2")

      expect(minValue % minValue).toEqual("0")
      expect(minValue % minus1).toEqual("0")
      expect(minValue % minus2).toEqual("0")
      expect(minValue % minus3).toEqual("fffffffffffffffe")
      expect(minValue % minus4).toEqual("0")
      expect(minValue % minus15).toEqual("fffffffffffffff8")
      expect(minValue % minus16).toEqual("0")

      expect(`7632147899696541255L` % minValue).toEqual("69ead613f2a7ee47")
      expect(`7632147899696541255L` % minus1).toEqual("0")
      expect(`7632147899696541255L` % minus2).toEqual("1")
      expect(`7632147899696541255L` % minus3).toEqual("0")
      expect(`7632147899696541255L` % minus4).toEqual("3")
      expect(`7632147899696541255L` % minus15).toEqual("0")
      expect(`7632147899696541255L` % minus16).toEqual("7")
    }

    it("should correctly implement toString") {
      expect(maxInt.toString).toEqual("2147483647")
      expect(fromInt(-50).toString).toEqual("-50")
      expect(fromInt(-1000000000).toString).toEqual("-1000000000")
      expect((maxInt + one).toString).toEqual("2147483648")
      expect(minInt.toString).toEqual("-2147483648")
    }

    it("should correctly implement fromDouble") {
      expect(fromDouble(4.5)).toEqual("4")
      expect(fromDouble(-4.5)).toEqual("fffffffffffffffc")
    }

    it("should correctly implement toDouble") {
      expect(fromInt(5).toDouble).toEqual(5.0)
      expect((maxInt + one).toDouble).toEqual(2147483648.0)
    }

    it("should correctly implement numberOfLeadingZeros") {
      expect(fromInt(0).numberOfLeadingZeros).toEqual(64)
      expect(fromInt(1).numberOfLeadingZeros).toEqual(63)
      expect(fromInt(-1).numberOfLeadingZeros).toEqual(0)
      expect(fromInt(2).numberOfLeadingZeros).toEqual(62)
    }

    it("should implement hashCode() according to spec in j.l.Long") {
      expect(fromInt(0).hashCode()).toEqual(0)
      expect(fromInt(55).hashCode()).toEqual(55)
      expect(fromInt(-12).hashCode()).toEqual(11)
      expect(fromInt(10006548).hashCode()).toEqual(10006548)
      expect(fromInt(-1098748).hashCode()).toEqual(1098747)

      expect(`613354684553L`.hashCode()).toEqual(-825638905)
      expect(`9863155567412L`.hashCode()).toEqual(1910653900)
      expect(`3632147899696541255L`.hashCode()).toEqual(1735398658)
      expect(`7632147899696541255L`.hashCode()).toEqual(-1689438124)
    }

    // scalastyle:on disallow.space.before.token disallow.space.after.token
  }
}
