package com.rbc.timemanagmentservice.model.error;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by russbaker on 3/21/16.
 */
@XmlRootElement(name = "Error")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Error", propOrder = {"id", "type", "messages", "status"})
public class ErrorPayload {
    public ErrorPayload() {
    }

    private ErrorPayload(Exception ex) {
        id = String.valueOf(System.currentTimeMillis());
        type = ex.getClass().getSimpleName();
        messages.add(ex.getMessage());
    }

        @XmlElement(name = "id")
        private String id;

        @XmlElement(name = "type")
        private String type;

        @XmlElement(name = "messages")
        private List<String> messages = new ArrayList<>();

        @XmlElement(name = "status")
        private int status;





        public String getType() {
            return type;
        }

        public List<String> getMessages() {
            return messages;
        }

        public int getStatus() {
            return status;
        }

        public String getId() {
            return id;
        }

    }
