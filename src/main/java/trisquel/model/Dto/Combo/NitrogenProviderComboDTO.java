package trisquel.model.Dto.Combo;

import trisquel.model.NitrogenProvider;

import java.util.List;

public class NitrogenProviderComboDTO {
    NitrogenProvider defaultConfig;
    List<NitrogenProvider> providers;

    public NitrogenProviderComboDTO(NitrogenProvider defaultConfig, List<NitrogenProvider> providers) {
        this.defaultConfig = defaultConfig;
        this.providers = providers;
    }

    public NitrogenProviderComboDTO(List<NitrogenProvider> providers) {
        this.providers = providers;
    }

    public NitrogenProvider getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(NitrogenProvider defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public List<NitrogenProvider> getProviders() {
        return providers;
    }

    public void setProviders(List<NitrogenProvider> providers) {
        this.providers = providers;
    }
}
