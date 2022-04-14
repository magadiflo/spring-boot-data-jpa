package com.bolsadeideas.springboot.app.view.pdf;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.bolsadeideas.springboot.app.models.entity.Factura;
import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

//El mismo nombre que retorna el método ver de FacturaController
//La idea es convertir el contenido html de esa vista en pdf
@Component("factura/ver")
public class FacturaPdfView extends AbstractPdfView {

	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		// En el método ver de FacturaController, se envía en el model la "factura"
		// Aquí capturamos ese objeto
		Factura factura = (Factura) model.get("factura");

		PdfPTable tabla1 = new PdfPTable(1);
		tabla1.addCell("Datos del cliente");
		tabla1.addCell(factura.getCliente().getNombre().concat(" ").concat(factura.getCliente().getApellido()));
		tabla1.addCell(factura.getCliente().getEmail());
		
		PdfPTable tabla2 = new PdfPTable(1);
		tabla2.addCell("Datos de la factura");
		tabla2.addCell("Folio: " + factura.getId());
		tabla2.addCell("Descripción: " + factura.getDescripcion());
		tabla2.addCell("Fecha: " + factura.getCreateAt());
		
		document.add(tabla1);
		document.add(tabla2);		
	}

}
