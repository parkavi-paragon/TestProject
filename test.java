package com.paragon.pdf.main;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.PDFTextStripperByArea;
import org.apache.commons.lang.StringEscapeUtils;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.FilteredTextRenderListener;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
//import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.RegionTextRenderFilter;
import com.itextpdf.text.pdf.parser.RenderFilter;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import java.io.IOException;


public class PdfTextExtractor {

	static String imagename="";
	static String sRealPath="";
	static String in_imgPath="";
	public static String outputdir="";
	static String cropPAth="";
	public static  ArrayList<ImageCoordinates> lists =new ArrayList<ImageCoordinates>();
	//private PDFImageCoordinates PDFImageCoordinates;
	 static String texts;

	public Coords getCoords(HttpServletRequest request) {

		Coords objCoords = new Coords();
		double imageh;
		double imagey;
		double imagex;
		double imagew;
		double imagexend;
		double imageyend;
		double x = Double.parseDouble(request.getParameter("x"));
		double y = Double.parseDouble(request.getParameter("y"));
		double w = Double.parseDouble(request.getParameter("w"));
		double h = Double.parseDouble(request.getParameter("h"));
		double xend= x+w;
		double yend = y+h;
		/*int x_font = Integer.parseInt(request.getParameter("x"));
		int y_font = Integer.parseInt(request.getParameter("y"));
		int w_font =  Integer.parseInt(request.getParameter("w"));
		int h_font=Integer.parseInt(request.getParameter("h"));
		int  xend_font= (int) (x_font+w_font);
		int yend_font = y_font+h_font;
        System.out.println("xend ="+xend_font);
		System.out.println("yend="+xend_font);*/

		imagename = request.getParameter("imagename");
	    objCoords.setImagename(imagename);
	    try {
			System.out.println("imagename is"+imagename);
			imagename=imagename.substring(5,imagename.length());
		    System.out.println("no is"+imagename);
		     sRealPath = request.getSession().getServletContext().getRealPath("/uploadfile/page_000" + imagename+".pdf");
		     in_imgPath=request.getSession().getServletContext().getRealPath("/uploadfile/jpgimg");
		    outputdir=request.getSession().getServletContext().getRealPath("/downloadfile");
		    cropPAth=request.getSession().getServletContext().getRealPath("/Cropimage");
		    PDDocument pdf = PDDocument.load(new File(sRealPath));

			List allPages = pdf.getDocumentCatalog().getAllPages();
			PDPage pdpage = (PDPage) allPages.get(0);

			PrintImageLocations obj = new PrintImageLocations();
		    lists=obj.getList();
		    for(ImageCoordinates coordinates :lists)
		    {
		    	imagex=coordinates.getX();
		        imagey=coordinates.getY();
		        imagew=coordinates.getWidth();
		        imageh=coordinates.getHeight();
		        imagexend = imagex + imagew;
		        imageyend= imagey +imageh;

		        if((x<imagex)&&(imagex<xend)&&(y<imagey)&&(imagey<yend) ||(imagex<x)&&(x<imagexend)&&(imagey<y)&&(y<imageyend))
		        {

		        	objCoords.setX(imagex);
					objCoords.setY(imagey);
					objCoords.setW(imagew);
					objCoords.setH(imageh);

		        	return objCoords;

		        }

		    }

		    PDFTextStripperByArea stripper = new PDFTextStripperByArea();
			Rectangle2D region = new Rectangle2D.Double(x, y, w, h);
			String regionName = "region";

			stripper = new PDFTextStripperByArea();
			stripper.addRegion(regionName, region);
			stripper.extractRegions(pdpage);

			String sStrippedText = stripper.getTextForRegion(regionName);
            
			String sStrippedText_Copy =stripper.getTextForRegion(regionName);


			sStrippedText = StringEscapeUtils.escapeJava(sStrippedText);

			System.out.println(StringEscapeUtils.escapeJava(sStrippedText));

			String splString = sStrippedText.substring(0,6);
			if(splString.equals("\\u00A0"))
			{
				double x1= 20;
				double y1=2;
				double w1=19;
				double h1=5;
				double x_new = x+x1;
				double y_new =y+y1;
				double w_new =w+w1;
				double h_new= h+h1;
				double y_coord = 0 ;
				  PDFTextStripperByArea strippers = new PDFTextStripperByArea();
					Rectangle2D regions = new Rectangle2D.Double(x_new, y_new, w_new, h_new);
					String regionNames = "region";

					strippers = new PDFTextStripperByArea();
					strippers.addRegion(regionNames, regions);
					strippers.extractRegions(pdpage);

					String sStrippedText_new = strippers.getTextForRegion(regionNames);

					 if(!sStrippedText_new.isEmpty()) {
							String sStrippedTextSplitted_new[] = sStrippedText_new.split("\n");


							int iArSize_new = sStrippedTextSplitted_new.length;
							int iLength_new = 0;
							int iPosition_new = 0;
							for(int i = 0; i<iArSize_new;i++)
							{

								String sTemp = sStrippedTextSplitted_new[i];
								if(sTemp.length()>iLength_new)
								{
									iPosition_new = i;
									iLength_new=sTemp.length();

								}
							}

							Coords objFirstLine = APIInvikr.invokeAndGetCoords(sStrippedTextSplitted_new[0],w,h);
							Coords objLengthyLine = APIInvikr.invokeAndGetCoords(sStrippedTextSplitted_new[iPosition_new],w,h);
							Coords objLastLine = APIInvikr.invokeAndGetCoords(sStrippedTextSplitted_new[sStrippedTextSplitted_new.length-1],w,h);
							objCoords.setY(objFirstLine.getY());
							 y_coord = objFirstLine.getY();


					 }

					 if(!sStrippedText_Copy.isEmpty()) {
							String sStrippedTextSplitted_copy[] = sStrippedText_Copy.split("\n");

							System.out.println("arraysi"+sStrippedTextSplitted_copy);
							int iArSize_copy = sStrippedTextSplitted_copy.length;
							int iLength_copy = 0;
							int iPosition_copy = 0;
							for(int i = 0; i<iArSize_copy;i++)
							{
								System.out.println(sStrippedTextSplitted_copy);
								String sTemp = sStrippedTextSplitted_copy[i];
								if(sTemp.length()>iLength_copy)
								{
									iPosition_copy = i;
									iLength_copy=sTemp.length();
									System.out.println("textis"+sTemp);
								}
							}

							Coords objFirstLines = APIInvikr.invokeAndGetCoords(sStrippedTextSplitted_copy[0],w,h);
							Coords objLengthyLines = APIInvikr.invokeAndGetCoords(sStrippedTextSplitted_copy[iPosition_copy],w,h);
							Coords objLastLines = APIInvikr.invokeAndGetCoords(sStrippedTextSplitted_copy[sStrippedTextSplitted_copy.length-1],w,h);
							objCoords.setX(objLastLines.getX());
					    	objCoords.setW(objLastLines.getW());
					    	if(y_coord>objLastLines.getY())
					    	{
							objCoords.setH(((y_coord-objLastLines.getY())+objLastLines.getH()));
					    	}
					    	if(y_coord<objLastLines.getY())
					    	{
					    		objCoords.setH(((objLastLines.getY()-y_coord)+objLastLines.getH()));
					    	}
							objCoords.setText(sStrippedText_Copy);

					 }
			}

			else
			{
			sStrippedText=sStrippedText.replace("\\u00A0", " ");
			//sStrippedText=sStrippedText.replace("c\\u00E9d\\u00E9\\u00E0", "\\n");

		    sStrippedText = StringEscapeUtils.unescapeJava(sStrippedText);

            if(!sStrippedText.isEmpty()) {
				String sStrippedTextSplitted[] = sStrippedText.split("\n");

				System.out.println("arraysi"+sStrippedTextSplitted);
				int iArSize = sStrippedTextSplitted.length;
				int iLength = 0;
				int iPosition = 0;
				for(int i = 0; i<iArSize;i++)
				{
					System.out.println(sStrippedTextSplitted);
					String sTemp = sStrippedTextSplitted[i];
					if(sTemp.length()>iLength)
					{
						iPosition = i;
						iLength=sTemp.length();
						System.out.println("textis"+sTemp);
					}
				}

				System.out.println("getting text");
				Coords objFirstLine = APIInvikr.invokeAndGetCoords(sStrippedTextSplitted[0],w,h);
				Coords objLengthyLine = APIInvikr.invokeAndGetCoords(sStrippedTextSplitted[iPosition],w,h);
				Coords objLastLine = APIInvikr.invokeAndGetCoords(sStrippedTextSplitted[sStrippedTextSplitted.length-1],w,h);

				if(objFirstLine.getX()<objLengthyLine.getX()&&objFirstLine.getX()<objLastLine.getX())
					objCoords.setX(objFirstLine.getX());
				else if(objLengthyLine.getX()<objFirstLine.getX()&&objLengthyLine.getX()<objLastLine.getX())
					objCoords.setX(objLengthyLine.getX());
				else
					objCoords.setX(objLastLine.getX());
		    	objCoords.setY(objFirstLine.getY());
				objCoords.setW(objLengthyLine.getW());
				objCoords.setH(((objLastLine.getY()-objFirstLine.getY())+objLastLine.getH()));
				objCoords.setText(sStrippedText);

			}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

		return objCoords;
	}


public static String getTextFromCoords(Coords objCoords)
{
		String sStrippedText = "";

		try {
			System.out.println("getTextFromCoords"+imagename);


			System.out.println("path is"+sRealPath);
			PDDocument pdf = PDDocument.load(new File(sRealPath));
			List allPages = pdf.getDocumentCatalog().getAllPages();
			PDPage pdpage = (PDPage) allPages.get(0);

			PDFTextStripperByArea stripper = new PDFTextStripperByArea();
			//objCoords.setX(x);

			Rectangle2D region = new Rectangle2D.Double(objCoords.getX(), objCoords.getY(), objCoords.getW(), objCoords.getH());
			String regionName = "region";

			stripper = new PDFTextStripperByArea();
			stripper.addRegion(regionName, region);
			stripper.extractRegions(pdpage);

			sStrippedText = stripper.getTextForRegion(regionName);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return sStrippedText;
	}
}

