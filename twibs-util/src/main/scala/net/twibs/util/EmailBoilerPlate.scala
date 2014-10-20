package net.twibs.util

import java.io.InputStreamReader

import scala.xml.NodeSeq

import com.google.common.base.Charsets
import com.google.common.io.CharStreams
import com.googlecode.htmlcompressor.compressor.HtmlCompressor

class EmailBoilerPlate {
  def toString(title: String, header: NodeSeq, content: NodeSeq, footer: NodeSeq) = compressor.compress(doctype + surround(title, header, content, footer).toString())

  /* from https://github.com/mailchimp/email-blueprints/blob/master/templates/simple-basic.html */
  def surround(title: String, header: NodeSeq, content: NodeSeq, footer: NodeSeq) =
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta property="og:title" content={title} />
        <title>{title}</title>
		<style type="text/css">
            {styles}
		</style>
	</head>
    <body leftmargin="0" marginwidth="0" topmargin="0" marginheight="0" offset="0">
    	<center>
        	<table border="0" cellpadding="0" cellspacing="0" height="100%" width="100%" id="backgroundTable">
            	<tr>
                	<td align="center" valign="top">
                    	<table border="0" cellpadding="0" cellspacing="0" width="600" id="container">
                        	<tr>
                            	<td align="center" valign="top">
                                	<table border="0" cellpadding="10" cellspacing="0" width="600" id="headerTable">
                                        <tr>
                                            <td class="headerContent">
                                                {header}
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        	<tr>
                            	<td align="center" valign="top">
                                	<table border="0" cellpadding="10" cellspacing="0" width="600" id="contentTable">
                                    	<tr>
                                            <td valign="top" class="bodyContent">
                                                {content}
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        	<tr>
                            	<td align="center" valign="top">
                                	<table border="0" cellpadding="10" cellspacing="0" width="600" id="footerTable">
                                    	<tr>
                                        	<td valign="top" class="footerContent">
                                                {footer}
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                        <br/>
                    </td>
                </tr>
            </table>
        </center>
    </body>
</html>

  val styles = LessCssParserFactory.createParser(load).parse("/email.less")

  val compressor = {
    val ret = new HtmlCompressor()
    ret.setRemoveSurroundingSpaces("br,p,head,body,html,article,section,nav,dt,dd,h1,h2,h3,h4,h5,h6,script,li,ul,ol,meta,link")
    ret
  }

  def load(name: String): Option[String] = Some(CharStreams.toString(new InputStreamReader(getClass.getResourceAsStream(name), Charsets.UTF_8)))

  def doctype = """<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">"""
}
