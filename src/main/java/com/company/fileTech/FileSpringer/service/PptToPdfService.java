package com.company.fileTech.FileSpringer.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

@Service
public class PptToPdfService {

    public ByteArrayOutputStream convertPptToPdf(InputStream pptInputStream) throws Exception {
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        SlideShow ppt = new XMLSlideShow(pptInputStream);

        Document document = new Document();
        PdfWriter.getInstance(document, pdfOutputStream);
        document.open();

        Dimension pgsize = ppt.getPageSize();
        BufferedImage img;
        ByteArrayOutputStream imgOutputStream;

        for (XSLFSlide slide : ((XMLSlideShow) ppt).getSlides()) {
            img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = img.createGraphics();
            graphics.setPaint(Color.white);
            graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));

            // Render each slide
            slide.draw(graphics);
            graphics.dispose();

            imgOutputStream = new ByteArrayOutputStream();
            ImageIO.write(img, "png", imgOutputStream);

            Image slideImage = Image.getInstance(imgOutputStream.toByteArray());
            slideImage.scaleToFit(document.getPageSize().getWidth(), document.getPageSize().getHeight());
            document.add(slideImage);
        }

        document.close();
        return pdfOutputStream;
    }
}
