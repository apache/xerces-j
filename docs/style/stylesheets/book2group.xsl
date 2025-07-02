<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:template match="/book/faqs">
    <group title="{@title}">
      <xsl:apply-templates select="faq"/>
    </group>
  </xsl:template>

  <xsl:template match="faq">
    <entry title="{@title}" id="{@id}">
      <xsl:apply-templates 
          select="document(concat('../../', @source))"
	      mode='faqs-faq'/>
    </entry>
  </xsl:template>

  <xsl:template match="faq" mode='faqs-faq'>
    <voice>
      <xsl:if test="string-length(@title)=0">
        <xsl:value-of select="q"/>
      </xsl:if>
      <xsl:if test="string-length(@title)>0">
        <xsl:value-of select="@title"/>
      </xsl:if>
    </voice>
  </xsl:template>
  
</xsl:stylesheet>