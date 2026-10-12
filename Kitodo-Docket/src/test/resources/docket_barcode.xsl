<?xml version="1.0" encoding="utf-8"?>
<!--
 *
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 *
 -->
<!-- Minimal docket template that emits two barcode4j code128 elements (one for
     the process title, one for the Signatur property). Used by
     BarcodeExportRegressionTest to verify that the barcode4j FOP extension
     plugin still renders barcodes with the bundled Apache FOP version. -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:kitodo="http://www.kitodo.org/logfile" exclude-result-prefixes="fo">
    <xsl:output method="xml" indent="yes"/>
    <xsl:template match="kitodo:process">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="A5-landscape" page-width="14.8cm" page-height="21.0cm"
                                       margin-left="1cm" margin-top="1cm"
                                       margin-right="1cm">
                    <fo:region-body/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="A5-landscape">
                <fo:flow flow-name="xsl-region-body">
                    <fo:block font-family="sans-serif" font-weight="bold" font-size="16pt" margin-top="20pt">
                        <xsl:value-of select="kitodo:title"/>
                    </fo:block>
                    <xsl:variable name="titlebarcode" select="kitodo:title"/>
                    <fo:block text-align="center" margin-top="20pt">
                        <fo:instream-foreign-object>
                            <barcode:barcode xmlns:barcode="http://barcode4j.krysalis.org/ns"
                                             message="{$titlebarcode}">
                                <barcode:code128>
                                    <barcode:module-width>0.21mm</barcode:module-width>
                                    <barcode:height>20mm</barcode:height>
                                </barcode:code128>
                            </barcode:barcode>
                        </fo:instream-foreign-object>
                    </fo:block>
                    <xsl:variable name="signaturbarcode">
                        <xsl:for-each select="kitodo:originals/kitodo:original">
                            <xsl:for-each select="kitodo:properties/kitodo:property">
                                <xsl:if test="@propertyIdentifier ='Signatur'">
                                    <xsl:value-of select="@value"/>
                                </xsl:if>
                            </xsl:for-each>
                        </xsl:for-each>
                    </xsl:variable>
                    <xsl:if test="$signaturbarcode !=''">
                        <fo:block text-align="center" margin-top="20pt">
                            <fo:instream-foreign-object>
                                <barcode:barcode xmlns:barcode="http://barcode4j.krysalis.org/ns"
                                                 message="{$signaturbarcode}">
                                    <barcode:code128>
                                        <barcode:module-width>0.21mm</barcode:module-width>
                                        <barcode:height>20mm</barcode:height>
                                    </barcode:code128>
                                </barcode:barcode>
                            </fo:instream-foreign-object>
                        </fo:block>
                    </xsl:if>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>
