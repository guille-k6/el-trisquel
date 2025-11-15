package trisquel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trisquel.model.Dto.Combo.NitrogenProviderComboDTO;
import trisquel.model.NitrogenProvider;
import trisquel.service.ConfigurationService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/nitrogen_provider")
public class NitrogenProviderController {

    private final ConfigurationService configService;

    @Autowired
    public NitrogenProviderController(ConfigurationService configService) {
        this.configService = configService;
    }

    @GetMapping
    public List<NitrogenProvider> getNitrogenProviders() {
        return Arrays.stream(NitrogenProvider.values()).toList();
    }

    @GetMapping("/combo")
    public NitrogenProviderComboDTO getProvidersForCombo() {
        Optional<NitrogenProvider> provider = configService.getDefaultNitrogenProvider();
        if (provider.isPresent()) {
            return new NitrogenProviderComboDTO(provider.get(), Arrays.stream(NitrogenProvider.values()).toList());
        }
        return new NitrogenProviderComboDTO(Arrays.stream(NitrogenProvider.values()).toList());
    }
}
