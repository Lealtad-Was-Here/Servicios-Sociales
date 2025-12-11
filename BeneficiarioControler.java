package org601.controller;

import org601.entity.Beneficiario;
import org601.service.BeneficiarioService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
@RequestMapping("/api/beneficiarios")
public class BeneficiarioController {
    private final BeneficiarioService service;
    public BeneficiarioController(BeneficiarioService service) { this.service = service; }

    @PostMapping
    public Beneficiario create(@RequestBody Beneficiario b) { return service.save(b); }

    @GetMapping
    public List<Beneficiario> list() { return service.list(); }

    @GetMapping("/{id}")
    public ResponseEntity<Beneficiario> get(@PathVariable Long id) {
        return service.get(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Beneficiario> update(@PathVariable Long id, @RequestBody Beneficiario b) {
        return service.get(id).map(existing -> {
            existing.setNombre(b.getNombre());
            existing.setEdad(b.getEdad());
            existing.setTipoServicio(b.getTipoServicio());
            return ResponseEntity.ok(service.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel() throws Exception {
        List<Beneficiario> list = service.list();
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Beneficiarios");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Nombre");
            header.createCell(2).setCellValue("Edad");
            header.createCell(3).setCellValue("TipoServicio");
            int r = 1;
            for (Beneficiario b : list) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(b.getId());
                row.createCell(1).setCellValue(b.getNombre());
                row.createCell(2).setCellValue(b.getEdad());
                row.createCell(3).setCellValue(b.getTipoServicio());
            }
            wb.write(out);
            byte[] bytes = out.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDisposition(ContentDisposition.attachment().filename("beneficiarios.xlsx").build());
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        }
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf() throws Exception {
        List<Beneficiario> list = service.list();
        Document document = new Document();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph("Lista de Beneficiarios"));
            for (Beneficiario b : list) {
                document.add(new Paragraph(String.format("%d - %s - %d - %s", b.getId(), b.getNombre(), b.getEdad(), b.getTipoServicio())));
            }
            document.close();
            byte[] bytes = out.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment().filename("beneficiarios.pdf").build());
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        }
    }
}
