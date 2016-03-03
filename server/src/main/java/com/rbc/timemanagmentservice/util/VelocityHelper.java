package com.rbc.timemanagmentservice.util;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * Created by russbaker on 3/2/16.
 */
@Component
public class VelocityHelper {
    private final VelocityEngine velocityEngine;
    private final VelocityContext velocityContext;

    @Autowired
    public VelocityHelper(VelocityEngine velocityEngine, VelocityContext velocityContext) {
        this.velocityEngine = velocityEngine;
        this.velocityContext = velocityContext;
    }


    public String convertTemplateToString(final String location, final Map<String,Object> templateVariables){
        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, location, "UTF-8", templateVariables);
    }
}
