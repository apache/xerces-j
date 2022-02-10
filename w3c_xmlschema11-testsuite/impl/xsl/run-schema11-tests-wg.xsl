<!--
   XML Schema 1.1 test-suite XSLT stylesheet.
   
   @author: Mukul Gandhi IBM
   
   @creation date: 2011-12-08
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                
				xmlns:xlink="http://www.w3.org/1999/xlink"
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				xmlns:ts="http://www.w3.org/XML/2004/xml-schema-test-suite/"
				xmlns:java="http://xml.apache.org/xalan/java"
				xmlns:redirect="http://xml.apache.org/xalan/redirect"
				xmlns:exslt="http://exslt.org/common"				
				exclude-result-prefixes="xlink xsi ts java exslt"
				extension-element-prefixes="redirect" version="1.0">
       
	<xsl:output method="html"/>
	
	<xsl:param name="vendorId"/>
	
	<xsl:param name="vendorUrl"/>
	
	<xsl:variable name="testColorCode">
	   <pass>#CCFF99</pass>
	   <fail>#FFCC99</fail>
	   <undecided>yellow</undecided>
	</xsl:variable>
	
	<xsl:variable name="dateFormatter" select="java:java.text.SimpleDateFormat.new('yyyy-MM-dd HH:mm:ss')"/>
	<xsl:variable name="currentDate" select="java:java.util.Date.new()"/>
	
	<!-- 
	   The starting template.
	-->
	<xsl:template match="ts:testSuite">
	   <html>
	      <head>
		    <title>XML Schema 1.1 test-suite results</title>
		  </head>
		  <body>	  
			 <h3><u>Apache Xerces XML Schema 1.1 test suite results</u></h3>			 
			 <h3><a href="{$vendorUrl}"><xsl:value-of select="$vendorId" /></a> XML Schema 1.1 tests</h3>
			 <p>Report generated on : <xsl:value-of select="java:format($dateFormatter, $currentDate)" /></p>
			 <xsl:variable name="testSuiteResults">
                            <xsl:apply-templates select="ts:testSetRef/@xlink:href" mode="testSet" />
			 </xsl:variable>			 
			 <h3><font color="green">Test-suite run summary :</font></h3>
			 <table border="1">
			    <tr>
				   <td align="center">Total tests</td>
				   <td align="center">Tests passed</td>
				   <td align="center">Success %</td>
				</tr>
				<xsl:variable name="totalTests">
                   <xsl:value-of select="count(document(ts:testSetRef/@xlink:href)//ts:schemaTest[not(@version = '1.0')] | 
				                               document(ts:testSetRef/@xlink:href)//ts:instanceTest[not(@version = '1.0')])" />											   
				</xsl:variable>
				<xsl:variable name="testsPassed" select="count(exslt:node-set($testSuiteResults)/tr/td[@id = 'pass'])" />
				<tr>
				   <td align="center"><xsl:value-of select="$totalTests" /></td>
				   <td align="center"><xsl:value-of select="$testsPassed" /></td>
				   <td align="center"><xsl:value-of select="format-number(($testsPassed * 100) div $totalTests, '##.##')" /></td>
				</tr>
				<redirect:write select="concat('../reports/', $vendorId, '_xsd11_testsuite_results.xml')">
                                   <result vendorId="{$vendorId}">
				      <totalTests><xsl:value-of select="$totalTests" /></totalTests>
				      <testsPassed><xsl:value-of select="$testsPassed" /></testsPassed>
				      <successPerct><xsl:value-of select="format-number(($testsPassed * 100) div $totalTests, '##.##')" /></successPerct>
				   </result>
                                </redirect:write>
			 </table>
			 <br/>
			 <h3><font color="green">Detailed tests status :</font></h3>
			 <table border="1">
			    <xsl:call-template name="generateReportHeader" />
				<!-- render table contents -->
				<xsl:call-template name="renderTableContents">
				   <xsl:with-param name="tableRows" select="exslt:node-set($testSuiteResults)/tr" />
				</xsl:call-template>				
			 </table>	 
	      </body>
	   </html>
    </xsl:template>
	
	
	<!--
       Render table rows. Leave-out last "td" cell designated to track schema and "instance document" validity.
    -->	
	<xsl:template name="renderTableContents">
	   <xsl:param name="tableRows" />
	   
	   <xsl:for-each select="$tableRows">
	      <tr>
		    <xsl:copy-of select="td[not(@id = 'pass')]" />
		  </tr>
	   </xsl:for-each>
	</xsl:template>
	
	
	<!--
	   Template to process a testSet (identified by the URI of test-set).
	-->
	<xsl:template match="@xlink:href" mode="testSet">
	   <xsl:variable name="testSetDoc" select="document(.)" />
	      <tr>
	         <td>&#160;</td>
		     <td align="center" bgcolor="#FFFF99"><xsl:value-of select="$testSetDoc/ts:testSet/@name" /></td>
		     <td>&#160;</td>
		     <td>&#160;</td>
		     <td>&#160;</td>
		     <td>&#160;</td>
		     <td>&#160;</td>
	      </tr>
	      <xsl:apply-templates select="$testSetDoc/ts:testSet/ts:testGroup" />
	</xsl:template>
	
	
	<!--
	   Template to process a testGroup.
	-->
	<xsl:template match="ts:testGroup">
	      <tr>
	        <td>&#160;</td>
		    <td>&#160;</td>
			<td align="center"><xsl:value-of select="@name" /></td>
		    <td>&#160;</td>
		    <td>&#160;</td>
		    <td>&#160;</td>
		    <td>&#160;</td>
	      </tr>
	      <xsl:variable name="schemaTestResult">
	         <xsl:apply-templates select="ts:schemaTest[not(@version = '1.0')]"/>
	      </xsl:variable>
		  <xsl:if test="exslt:node-set($schemaTestResult)/tr">
	         <tr>
	            <!-- don't produce the element node "schDocValidity" in HTML output. it's an internal meta data. -->
	            <xsl:copy-of select="exslt:node-set($schemaTestResult)/tr/*[position() &lt; last()]"/>
	         </tr>
		  </xsl:if>
	      <xsl:apply-templates select="ts:instanceTest[not(@version = '1.0')]">
		     <xsl:with-param name="schDocValidity" select="exslt:node-set($schemaTestResult)/tr/schDocValidity"/>
	      </xsl:apply-templates>
	</xsl:template>
	
	
	<!--
	   Template to invoke a "schema test".
	-->
	<xsl:template match="ts:schemaTest">   
	   <tr>
	     <!-- generating serial no for the test case -->
		 <td align="center"><xsl:value-of select="count(preceding::ts:schemaTest[not(@version = '1.0')] | preceding::ts:instanceTest[not(@version = '1.0')]) + 1" />.</td>
		 <td>&#160;</td>
		 <td>&#160;</td>
		 <td align="center"><xsl:value-of select="@name" /></td>
		 <td>&#160;</td>
		 <xsl:variable name="schemaDocValidity">
		    <xsl:choose>
			   <xsl:when test="java:XMLValidatorHelper2.isSchemaValid(ts:schemaDocument[1]/@xlink:href)">
			      valid
			   </xsl:when>
			   <xsl:otherwise>
			      invalid
			   </xsl:otherwise>
			</xsl:choose>
		 </xsl:variable>

		 <xsl:variable name="effectiveExpValidity">
		    <xsl:choose>
			   <xsl:when test="ts:expected[@version = '1.1']">
			      <xsl:value-of select="ts:expected[@version = '1.1']/@validity"/>
			   </xsl:when>
			   <xsl:otherwise>
			      <xsl:value-of select="ts:expected/@validity"/>
			   </xsl:otherwise>
			</xsl:choose>
		 </xsl:variable>
		 
		 <xsl:variable name="clrStatus">
		    <xsl:call-template name="getTestCaseColorStatus">
			   <xsl:with-param name="expectedValidity" select="normalize-space($effectiveExpValidity)" />
			   <xsl:with-param name="actualValidity" select="normalize-space($schemaDocValidity)" />
			</xsl:call-template>
		 </xsl:variable>
		 <td align="center" bgcolor="{normalize-space($clrStatus)}"><xsl:value-of select="normalize-space($effectiveExpValidity)" /></td>		 
		 <td align="center" bgcolor="{normalize-space($clrStatus)}"><xsl:value-of select="normalize-space($schemaDocValidity)" /></td>
		 <!-- a hidden table cell is kept here to track test result -->
		 <xsl:if test="normalize-space($schemaDocValidity) = normalize-space($effectiveExpValidity)">
	        <td id="pass" />
	     </xsl:if>
		 <!-- save internal meta data, to prohibit instance tests to run if schema test has failed -->
         <schDocValidity><xsl:value-of select="normalize-space($schemaDocValidity)"/></schDocValidity>		 
	   </tr>	   
	</xsl:template>
	
	<!--
	   Template to invoke an instance test.
	-->
	<xsl:template match="ts:instanceTest">
	   <xsl:param name="schDocValidity"/>
	   
	   <tr>
	     <!-- generating serial no for the test case -->
		 <td align="center"><xsl:value-of select="count(preceding::ts:schemaTest[not(@version = '1.0')] | preceding::ts:instanceTest[not(@version = '1.0')]) + 1" />.</td>
		 <td>&#160;</td>
		 <td>&#160;</td>
		 <td>&#160;</td>
		 <td align="center"><xsl:value-of select="@name" /></td>
         <xsl:variable name="effectiveExpValidity" select="ts:expected/@validity"/>
		 <xsl:choose>
		    <xsl:when test="$schDocValidity = 'valid'">
		       <xsl:variable name="instanceDocValidity" select="java:XMLValidatorHelper2.getInstanceDocValidity(ts:instanceDocument/@xlink:href,
			                                                                   preceding-sibling::ts:schemaTest[1]/ts:schemaDocument[1]/@xlink:href)"/>
		       <xsl:variable name="clrStatus">
		          <xsl:call-template name="getTestCaseColorStatus">
			        <xsl:with-param name="expectedValidity" select="normalize-space($effectiveExpValidity)" />
			        <xsl:with-param name="actualValidity" select="normalize-space($instanceDocValidity)" />
			      </xsl:call-template>
		       </xsl:variable>
		       <td align="center" bgcolor="{normalize-space($clrStatus)}"><xsl:value-of select="normalize-space($effectiveExpValidity)" /></td>
		       <td align="center" bgcolor="{normalize-space($clrStatus)}"><xsl:value-of select="normalize-space($instanceDocValidity)" /></td>
		       <!-- a hidden table cell to track test result, status -->
		       <xsl:if test="normalize-space($instanceDocValidity) = normalize-space($effectiveExpValidity)">
	              <td id="pass" />
	           </xsl:if>
		    </xsl:when>
		    <xsl:otherwise>
			    <!-- if schema is 'invalid', don't run the corresponding instance tests and set instance test results as 'pass' -->
		        <td align="center" bgcolor="{exslt:node-set($testColorCode)/pass}"><xsl:value-of select="normalize-space($effectiveExpValidity)" /></td>
		        <td align="center" bgcolor="{exslt:node-set($testColorCode)/pass}"><xsl:value-of select="normalize-space($effectiveExpValidity)" /></td>
				<!-- a hidden table cell to track test result, status -->
		        <td id="pass" />
		    </xsl:otherwise>
		  </xsl:choose>
	   </tr>	   
	</xsl:template>
	
	<!--
	   Generate report header.
	-->
	<xsl:template name="generateReportHeader">
	   <tr>
		  <td align="center"><b>SL. No.</b></td>
		  <td align="center"><b>TestSet name</b></td>
		  <td align="center"><b>TestGroup name</b></td>
		  <td align="center"><b>Schema Test name</b></td>
		  <td align="center"><b>Instance Test name</b></td>
		  <td align="center"><b>Expected Test status</b></td>
		  <td align="center"><b>Actual Test status</b></td>
	   </tr>
	</xsl:template>	
	
	<!--
	   Get color code value to display, for a test-case success or failure.
	-->
	<xsl:template name="getTestCaseColorStatus">
	   <xsl:param name="expectedValidity" />
	   <xsl:param name="actualValidity" />
	   
	   <xsl:choose>
		  <xsl:when test="$expectedValidity = $actualValidity">
			 <xsl:value-of select="exslt:node-set($testColorCode)/pass"/>
		  </xsl:when>
		  <xsl:otherwise>
		     <xsl:value-of select="exslt:node-set($testColorCode)/fail"/>
		  </xsl:otherwise>
	   </xsl:choose>
	</xsl:template>
	
</xsl:stylesheet>