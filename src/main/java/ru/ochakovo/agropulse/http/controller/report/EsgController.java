package ru.ochakovo.agropulse.http.controller.report;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.ochakovo.agropulse.dto.report.esg.EsgPageDto;
import ru.ochakovo.agropulse.service.report.esg.EsgIndicatorsService;

import java.util.List;

@Controller
@RequestMapping("/esg")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('OPERATOR')")
public class EsgController {

    private final EsgIndicatorsService esgIndicatorsService;

    @GetMapping
    public String page(@RequestParam(required = false) Integer year, Model model) {
        EsgPageDto page = esgIndicatorsService.getPage(year);
        model.addAttribute("esgPage", page);
        model.addAttribute("availableYears", esgIndicatorsService.getAvailableYears(page.selectedYear()));
        model.addAttribute("canSaveEsg", true);
        return "reports/esg/esg";
    }

    @PostMapping
    public String save(@RequestParam int year,
                       @RequestParam(required = false) List<Long> indicatorId,
                       @RequestParam(required = false) List<String> value,
                       RedirectAttributes redirectAttributes) {
        try {
            esgIndicatorsService.saveValues(year, indicatorId, value);
        } catch (ResponseStatusException ex) {
            redirectAttributes.addFlashAttribute("esgError", ex.getReason());
        }
        return "redirect:/esg?year=" + year;
    }
}
