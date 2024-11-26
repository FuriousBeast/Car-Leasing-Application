package com.trimble.trimblecars.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.trimble.trimblecars.entity.Car;
import com.trimble.trimblecars.entity.Lease;
import com.trimble.trimblecars.utils.CommonUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * ReportService.java
 *
 * @author Nandhakumar N (nandhakumarn@nmsworks.co.in)
 * @module com.trimble.trimblecars.service
 * @created Nov 25, 2024
 */

@Service
public class ReportService
{
    public byte[] generateLeaseHistoryReport(List<Lease> leases)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdfDocument = new PdfDocument(writer);
                 Document document = new Document(pdfDocument))
        {

            Paragraph title = new Paragraph("Trimble Cars")
                    .setBold()
                    .setFontSize(20)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setMarginBottom(20); // Add spacing after the title
            document.add(title);
            document.add(new Paragraph("Lease History Report").setBold().setFontSize(16));
            document.add(new Paragraph("Generated on: " + java.time.LocalDate.now()).setFontSize(10));
            document.add(new Paragraph("\n"));

            // Handle Empty Lease List
            if(CommonUtils.nullOrEmpty(leases))
            {
                Paragraph noData = new Paragraph("No lease history data available.")
                        .setFontSize(14)
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                        .setMarginTop(50); // Add spacing from the title
                document.add(noData);
            }
            else
            {
                // Create Table with Column Widths
                Table table = new Table(new float[]{2, 4, 4, 3, 3, 4, 4, 3});

                table.setMaxWidth(UnitValue.createPercentValue(100));
                table.addHeaderCell("Lease ID");
                table.addHeaderCell("Car Model");
                table.addHeaderCell("Car Owner");
                table.addHeaderCell("Customer");
                table.addHeaderCell("Customer Id");
                table.addHeaderCell("Start Date");
                table.addHeaderCell("End Date");
                table.addHeaderCell("Status");

                for (Lease lease : leases) {
                    table.addCell(String.valueOf(lease.getId()));
                    Car car = lease.getCar();
                    table.addCell(car.getMake() + " " + car.getModel() + " " + car.getYear());
                    table.addCell(car.getOwner().getUsername());
                    table.addCell(lease.getCustomer().getUsername());
                    table.addCell(lease.getCustomer().getUserId().toString());
                    table.addCell(lease.getStartDate().toString());
                    table.addCell(lease.getEndDate() != null ? lease.getEndDate().toString() : "NA");
                    table.addCell(lease.getState().name());
                }

                document.add(table);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error generating lease history report", e);
        }

        return outputStream.toByteArray();
    }
}
