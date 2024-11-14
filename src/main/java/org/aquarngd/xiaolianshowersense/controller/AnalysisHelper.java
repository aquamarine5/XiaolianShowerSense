package org.aquarngd.xiaolianshowersense.controller;

import com.alibaba.fastjson2.JSONObject;
import org.aquarngd.xiaolianshowersense.ResidenceController;
import org.aquarngd.xiaolianshowersense.XiaolianAnalysis;
import org.aquarngd.xiaolianshowersense.XiaolianShowerSenseApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalysisHelper {
    private final XiaolianShowerSenseApplication xiaolianShowerSenseApplication;
    private final ResidenceController residenceController;

    public AnalysisHelper(XiaolianShowerSenseApplication xiaolianShowerSenseApplication, ResidenceController residenceController) {
        this.xiaolianShowerSenseApplication = xiaolianShowerSenseApplication;
        this.residenceController = residenceController;
    }

    @GetMapping("/analysis")
    public JSONObject GetAnalysis() {
        return new JSONObject();
    }

    @GetMapping("/force_analyse")
    public JSONObject ForceAnalyse() {
        residenceController.shouldUpdateAnalysis=true;
        return new JSONObject();
    }
}
