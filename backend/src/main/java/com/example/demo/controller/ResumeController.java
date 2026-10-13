package com.example.demo.controller;

import com.example.demo.dto.ResumeDTO;
import com.lowagie.text.pdf.BaseFont;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.thymeleaf.context.Context;
import org.thymeleaf.TemplateEngine;

import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {
    @Autowired
    private TemplateEngine templateEngine;
    @PostMapping("/generate")
    public ResponseEntity<byte[]> generate(@RequestBody ResumeDTO dto) throws Exception {
        // 1. 选模板
        String templateName = "resume" + dto.getTemplate() + ".html";

        // 2. Thymeleaf 上下文
        Context ctx = new Context();
        ctx.setVariable("name", dto.getName());
        System.out.println(dto.getName());
        ctx.setVariable("email", dto.getEmail());
        ctx.setVariable("phone", dto.getPhone());
        ctx.setVariable("skills", dto.getSkills());
        ctx.setVariable("educations", dto.getEducations());
        ctx.setVariable("experiences", dto.getExperiences());
        ctx.setVariable("avatar", dto.getZhaopian());

        // 3. 渲染 HTML
        String html = templateEngine.process(templateName, ctx);
        System.out.println(html);

        // 4. HTML → PDF（Flying Saucer）
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.getFontResolver().addFont(
                "C:/Windows/Fonts/simsun.ttc", // Windows系统
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED);
        renderer.setDocumentFromString(html);
        renderer.layout();
        if (renderer.getDocument() == null) {
            System.out.println("Failed to create document");
            // 返回错误响应
        }

        renderer.createPDF(out);
        System.out.println("PDF size: " + out.size() + " bytes");

// 如果PDF大小为0，说明生成失败
        if (out.size() == 0) {
            // 处理错误情况
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=resume.pdf");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }


}
