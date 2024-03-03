package com.in28minutes.rest.webservices.restfulwebservices;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IPdfTextLocation;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.pdfcleanup.CleanUpProperties;
import com.itextpdf.pdfcleanup.PdfCleanUpLocation;
import com.itextpdf.pdfcleanup.PdfCleanUpTool;
import com.itextpdf.pdfcleanup.PdfCleaner;
import com.itextpdf.pdfcleanup.autosweep.CompositeCleanupStrategy;
import com.itextpdf.pdfcleanup.autosweep.RegexBasedCleanupStrategy;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@OpenAPIDefinition(info =@Info(title="Todo APIS", version="1.0", description="TodoApp and HelloWorld"))
public class RestfulWebServicesApplication {
/*
	//@Value("file:/Users/fatihakguc/Desktop/10568471.pdf")
	@Value("file:/Users/fatihakguc/Downloads/01-rest-api-starting-code/src/main/resources/10568471.pdf")
	Resource pdf;
	@Value("file:/Users/fatihakguc/Downloads/01-rest-api-starting-code/src/main/resources/10568472.pdf")
	Resource pdfWritten;

	//@Value("/src/main/resources/photo.png")
	//Resource image;


	@Autowired
	ResourceLoader resourceLoader;
*/
	public static void main(String[] args) throws IOException {
		SpringApplication.run(RestfulWebServicesApplication.class, args);


	}
/*
	@Bean
	public String readWritePdf() throws IOException {


		PdfReader reader = new PdfReader(pdf.getFilename());
		PdfWriter writer = new PdfWriter(pdfWritten.getFilename());

		PdfDocument pdfDocument = new PdfDocument(reader,writer);
		Document doc = new Document(pdfDocument);
		pdfDocument.addNewPage(1);


		PdfPage page = pdfDocument.getPage(2);

		StringBuilder text = new StringBuilder();
		for (int i = 1; i <= 1; i++) {
			text.append(PdfTextExtractor.getTextFromPage(page));
			text.append("Fatih Akgüç Tugce Tugce2");
			System.out.println(text.toString());
		}

		Paragraph paragraph = new Paragraph().add(text.toString()).setFontSize(8);
		doc.add(paragraph);

		Table table = new Table(UnitValue.createPercentArray(2));
		table.addHeaderCell("#");
		table.addHeaderCell("company");
		table.addCell("name");
		table.addCell("Ericsson");
		doc.add(table);


		ImageData imageData = ImageDataFactory.create("photo.png");
		Image image = new Image(imageData).scaleAbsolute(50,50)
				.setFixedPosition(1, 10, 50);
		doc.add(image);

		doc.close();
		reader.close();


		return "Pdf is read and written!";

	}
*//*
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedMethods("*")
						.allowedOrigins("*");//#CHANGE //NOT RECOMMENDED FOR PRODUCTION
			}
		};
	}
*/

}
