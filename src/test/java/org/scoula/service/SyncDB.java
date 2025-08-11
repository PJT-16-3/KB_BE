package org.scoula.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.scoula.config.RootConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { OfficetelService.class, AptService.class,RootConfig.class })
@Log4j2
public class SyncDB {
    @Autowired
    private OfficetelService officetelService;

    @Autowired
    private AptService aptService;

    @Test
    void syncDB() {
        aptService.syncAptData();
        officetelService.syncOfficetelData();
    }
}
