package ru.ochakovo.agropulse.service.report.print;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.WaitUntilState;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ochakovo.agropulse.config.PrintProperties;
import ru.ochakovo.agropulse.dto.report.print.ReportPdfExportOptions;
import ru.ochakovo.agropulse.service.report.core.ReportService;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportPdfService {

    private static final String READ_FOOTER_LABEL_SCRIPT = "() => document.body.dataset.footerLabel || ''";

    private final Browser browser;
    private final PrintProperties printProperties;
    private final ReportService reportService;

    public byte[] generatePdf(Long reportId, ReportPdfExportOptions options, HttpServletRequest request) {
        reportService.requireExportPdf(reportId);

        String printUrl = buildPrintUrl(reportId, options);
        double timeoutMs = printProperties.timeoutSeconds() * 1000.0;
        URI baseUri = URI.create(printProperties.baseUrl());

        try (BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setDeviceScaleFactor(printProperties.deviceScaleFactor()))) {
            addSessionCookies(context, request, baseUri);

            Page page = context.newPage();
            page.navigate(
                    printUrl,
                    new Page.NavigateOptions()
                            .setWaitUntil(WaitUntilState.NETWORKIDLE)
                            .setTimeout(timeoutMs)
            );
            page.waitForSelector(
                    "[data-print-ready='true']",
                    new Page.WaitForSelectorOptions().setTimeout(timeoutMs)
            );

            String footerLabel = (String) page.evaluate(READ_FOOTER_LABEL_SCRIPT);

            return page.pdf(new Page.PdfOptions()
                    .setFormat("A4")
                    .setPrintBackground(true)
                    .setPreferCSSPageSize(true)
                    .setDisplayHeaderFooter(true)
                    .setHeaderTemplate("<div></div>")
                    .setFooterTemplate(buildPdfFooterTemplate(footerLabel)));
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Не удалось сформировать PDF отчёта. Убедитесь, что приложение доступно по "
                    + printProperties.baseUrl() + " и установлен Chromium для Playwright.", ex);
        }
    }

    private static String buildPdfFooterTemplate(String footerLabel) {
        String label = footerLabel == null || footerLabel.isBlank()
                ? "Финансовый аналитический отчёт"
                : footerLabel.strip();
        String safe = label
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
        return """
                <div style="width:100%%;font-size:9px;color:#9CA3AF;padding:0 40px;font-family:Inter,Arial,sans-serif;box-sizing:border-box;display:flex;justify-content:space-between;align-items:center;">
                  <span style="flex:1;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">%s</span>
                  <span style="flex-shrink:0;margin-left:16px;">Стр. <span class="pageNumber"></span></span>
                </div>
                """.formatted(safe);
    }

    private String buildPrintUrl(Long reportId, ReportPdfExportOptions options) {
        String base = printProperties.baseUrl().replaceAll("/$", "");
        return base + "/reports/" + reportId + "/print"
                + "?esg=" + options.esg()
                + "&tables=" + options.tables()
                + "&charts=" + options.charts()
                + "&recommendations=" + options.recommendations()
                + "&comment=" + options.comment()
                + "&chartDpr=" + printProperties.chartDevicePixelRatio();
    }

    private static void addSessionCookies(BrowserContext context, HttpServletRequest request, URI baseUri) {
        if (request == null || request.getCookies() == null) {
            return;
        }

        List<Cookie> cookies = new ArrayList<>();
        for (jakarta.servlet.http.Cookie servletCookie : request.getCookies()) {
            cookies.add(new Cookie(servletCookie.getName(), servletCookie.getValue())
                    .setDomain(baseUri.getHost())
                    .setPath("/")
                    .setHttpOnly(servletCookie.isHttpOnly())
                    .setSecure(servletCookie.getSecure()));
        }
        if (!cookies.isEmpty()) {
            context.addCookies(cookies);
        }
    }

    public static String sanitizeFileName(String reportName, Long reportId) {
        String base = reportName == null || reportName.isBlank()
                ? "financial-report-" + reportId
                : reportName.strip();
        String safe = base.replaceAll("[\\\\/:*?\"<>|]", "_").replaceAll("\\s+", "_");
        if (safe.isBlank()) {
            safe = "financial-report-" + reportId;
        }
        return safe + ".pdf";
    }

    public static String contentDispositionValue(String fileName) {
        String encoded = java.net.URLEncoder.encode(fileName, java.nio.charset.StandardCharsets.UTF_8)
                .replace("+", "%20");
        String ascii = fileName.replaceAll("[^\\x20-\\x7E]", "_");
        return "attachment; filename=\"" + ascii + "\"; filename*=UTF-8''" + encoded;
    }
}
