/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.lifecycle.run.data;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;

public abstract class BlackDuckRunData {
    private final boolean isOnline;

    protected BlackDuckRunData(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public static OfflineBlackDuckRunData offline() {
        return new OfflineBlackDuckRunData();
    }

    public static OnlineBlackDuckRunData online(BlackDuckServicesFactory blackDuckServicesFactory, PhoneHomeManager phoneHomeManager, BlackDuckServerConfig blackDuckServerConfig, long timeoutInMillis) {
        return OnlineBlackDuckRunData.fromFactory(phoneHomeManager, blackDuckServerConfig, blackDuckServicesFactory, timeoutInMillis);
    }

    public static OnlineBlackDuckRunData onlineNoPhoneHome(BlackDuckServicesFactory blackDuckServicesFactory, BlackDuckServerConfig blackDuckServerConfig, long timeoutInMillis) {
        return OnlineBlackDuckRunData.fromFactory(null, blackDuckServerConfig, blackDuckServicesFactory, timeoutInMillis);
    }

}
