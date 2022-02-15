<!--
   XML Schema 1.1 test-suite XSLT stylesheet.
   
   @author: Mukul Gandhi, mukulg@apache.org
   
   @creation date: 2022-02-10
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:java="http://xml.apache.org/xalan/java"
                exclude-result-prefixes="java"
                version="1.0">
       
   <xsl:output method="html"/>
   
   <xsl:variable name="result1Doc" select="document('../../reports/IBM_xsd11_testsuite_results.xml')"/>
   <xsl:variable name="result2Doc" select="document('../../reports/Saxonica_xsd11_testsuite_results.xml')"/>
   <xsl:variable name="result3Doc" select="document('../../reports/Oracle_xsd11_testsuite_results.xml')"/>
   <xsl:variable name="result4Doc" select="document('../../reports/Microsoft_xsd11_testsuite_results.xml')"/>
   <xsl:variable name="result5Doc" select="document('../../reports/Sun Microsystems_xsd11_testsuite_results.xml')"/>
   <xsl:variable name="result6Doc" select="document('../../reports/WG_xsd11_testsuite_results.xml')"/>
   
   <xsl:variable name="dateFormatter" select="java:java.text.SimpleDateFormat.new('yyyy-MM-dd HH:mm:ss')"/>
   <xsl:variable name="currentDate" select="java:java.util.Date.new()"/>
   
   <xsl:template match="/">
      <html>
        <head>
          <style>
	    td { text-align: center; padding: 5px;}
	    a { text-decoration: none; }
          </style>
          <title>Xmlschema11TestSuiteReports</title>
        </head>
        <h3><u>Apache Xerces XML Schema 1.1 test suite results</u></h3>
        <p>Report generated on : <xsl:value-of select="java:format($dateFormatter, $currentDate)"/></p>
        <br/>
        <table border="1">
           <tr>
              <td><b>Vendor</b></td>
              <td><b>Total tests</b></td>
              <td><b>Tests passed</b></td>
              <td><b>Success %</b></td>
              <td><b>Detailed reports</b></td>
           </tr>
           <tr>
             <td>IBM</td>
             <td><xsl:value-of select="$result1Doc/result/totalTests"/></td>
             <td><xsl:value-of select="$result1Doc/result/testsPassed"/></td>
             <td><xsl:value-of select="$result1Doc/result/successPerct"/></td>
             <td><a href="ibm_xsd11_testsuite_results.html" target="_blank">&lt;link&gt;</a></td>
           </tr>
           <tr>
	     <td>Saxonica</td>
	     <td><xsl:value-of select="$result2Doc/result/totalTests"/></td>
	     <td><xsl:value-of select="$result2Doc/result/testsPassed"/></td>
	     <td><xsl:value-of select="$result2Doc/result/successPerct"/></td>
	     <td><a href="saxon_xsd11_testsuite_results.html" target="_blank">&lt;link&gt;</a></td>
           </tr>
           <tr>
	     <td>Oracle</td>
	     <td><xsl:value-of select="$result3Doc/result/totalTests"/></td>
	     <td><xsl:value-of select="$result3Doc/result/testsPassed"/></td>
	     <td><xsl:value-of select="$result3Doc/result/successPerct"/></td>
	     <td><a href="oracle_xsd11_testsuite_results.html" target="_blank">&lt;link&gt;</a></td>
           </tr>
           <tr>
	     <td>Microsoft</td>
	     <td><xsl:value-of select="$result4Doc/result/totalTests"/></td>
	     <td><xsl:value-of select="$result4Doc/result/testsPassed"/></td>
	     <td><xsl:value-of select="$result4Doc/result/successPerct"/></td>
	     <td><a href="ms_xsd11_testsuite_results.html" target="_blank">&lt;link&gt;</a></td>
           </tr>
           <tr>
	     <td>Sun Microsystems</td>
	     <td><xsl:value-of select="$result5Doc/result/totalTests"/></td>
	     <td><xsl:value-of select="$result5Doc/result/testsPassed"/></td>
	     <td><xsl:value-of select="$result5Doc/result/successPerct"/></td>
	     <td><a href="sun_xsd11_testsuite_results.html" target="_blank">&lt;link&gt;</a></td>
           </tr>
           <tr>
	     <td>WG</td>
	     <td><xsl:value-of select="$result6Doc/result/totalTests"/></td>
	     <td><xsl:value-of select="$result6Doc/result/testsPassed"/></td>
	     <td><xsl:value-of select="$result6Doc/result/successPerct"/></td>
	     <td><a href="xmlschema_wg_xsd11_testsuite_results.html" target="_blank">&lt;link&gt;</a></td>
           </tr>
        </table>
        <br/>
        <p><b>Overall Test Suite run status:</b></p>
        <table border="1">
           <tr>
             <td><b>Total tests</b></td>
             <td><b>Tests passed</b></td>
             <td><b>Success %</b></td>
           </tr>
           <tr>
             <xsl:variable name="totalTests" select="$result1Doc/result/totalTests + $result2Doc/result/totalTests + $result3Doc/result/totalTests + $result4Doc/result/totalTests + $result5Doc/result/totalTests + $result6Doc/result/totalTests"/>
             <xsl:variable name="testsPassed" select="$result1Doc/result/testsPassed + $result2Doc/result/testsPassed + $result3Doc/result/testsPassed + $result4Doc/result/testsPassed + $result5Doc/result/testsPassed + $result6Doc/result/testsPassed"/>
	     <td><xsl:value-of select="$totalTests"/></td>
	     <td><xsl:value-of select="$testsPassed"/></td>
	     <td><xsl:value-of select="format-number(($testsPassed * 100) div $totalTests, '##.##')" /></td>
           </tr>
        </table>
      </html>
   </xsl:template>
   
</xsl:stylesheet>   