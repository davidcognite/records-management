/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_rm.classification;

import org.alfresco.module.org_alfresco_module_rm.classification.ClassificationServiceException.MissingConfiguration;
import org.alfresco.module.org_alfresco_module_rm.util.ServiceBaseImpl;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author Neil Mc Erlean
 * @since 3.0
 */
public class ClassificationServiceImpl extends ServiceBaseImpl
                                       implements ClassificationService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationServiceImpl.class); 

    private static final String[] LEVELS_KEY = new String[] { "org.alfresco",
                                                              "module.org_alfresco_module_rm",
                                                              "classification.levels" };
    private static final String[] REASONS_KEY = new String[] { "org.alfresco",
														       "module.org_alfresco_module_rm",
														       "classification.reasons" };


	static final String DEFAULT_CONFIG_DIRECTORY = "/alfresco/module/org_alfresco_module_rm/classification/";
	static final String DEFAULT_LEVELS_FILE = DEFAULT_CONFIG_DIRECTORY
			+ "rm-classification-levels.json";
	static final String DEFAULT_REASONS_FILE = DEFAULT_CONFIG_DIRECTORY
			+ "rm-classification-reasons.json";

    private AttributeService attributeService; // TODO What about other code (e.g. REST API) accessing the AttrService?

    /** The classification levels currently configured in this server. */
    private List<ClassificationLevel> configuredLevels;
    /** The classification reasons currently configured in this server. */
    private List<ClassificationReason> configuredReasons;

    private Configuration config = new Configuration(DEFAULT_LEVELS_FILE, DEFAULT_REASONS_FILE);

    public void setAttributeService(AttributeService service) { this.attributeService = service; }

    void initConfiguredClassificationLevels()
    {
        final List<ClassificationLevel> allPersistedLevels  = getPersistedLevels();
        final List<ClassificationLevel> configurationLevels = getConfigurationLevels();

		// Note! We cannot log the level names or even the size of these lists for security reasons.
		LOGGER.debug("Persisted classification levels: {}", loggableStatusOf(allPersistedLevels));
		LOGGER.debug("Classpath classification levels: {}", loggableStatusOf(configurationLevels));

        if (configurationLevels == null || configurationLevels.isEmpty())
        {
            throw new MissingConfiguration("Classification level configuration is missing.");
        }
        else if ( !configurationLevels.equals(allPersistedLevels))
        {
            attributeService.setAttribute((Serializable) configurationLevels, LEVELS_KEY);
            this.configuredLevels = configurationLevels;
        }
        else
        {
            this.configuredLevels = allPersistedLevels;
        }
    }
    
    void initConfiguredClassificationReasons() {
    	final List<ClassificationReason> allPersistedReasons  = getPersistedReasons();
        final List<ClassificationReason> configurationReasons = getConfigurationReasons();

		// Note! We cannot log the reasons or even the size of these lists for security reasons.
		LOGGER.debug("Persisted classification reasons: {}", loggableStatusOf(allPersistedReasons));
		LOGGER.debug("Classpath classification reasons: {}", loggableStatusOf(configurationReasons));

        if (configurationReasons == null || configurationReasons.isEmpty())
        {
            throw new MissingConfiguration("Classification reason configuration is missing.");
        }
        else if ( !configurationReasons.equals(allPersistedReasons))
        {
            attributeService.setAttribute((Serializable) configurationReasons, REASONS_KEY);
            this.configuredReasons = configurationReasons;
        }
        else
        {
            this.configuredReasons = allPersistedReasons;
        }
    }

    /** Helper method for debug-logging of sensitive lists. */
    private String loggableStatusOf(List<?> l)
    {
        if      (l == null)   { return "null"; }
        else if (l.isEmpty()) { return "empty"; }
        else                  { return "non-empty"; }
    }

    /**
     * Gets the list (in descending order) of classification levels - as persisted in the system.
     * @return the list of classification levels if they have been persisted, else {@code null}.
     */
    List<ClassificationLevel> getPersistedLevels() {
        return authenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<List<ClassificationLevel>>()
        {
            @Override
            @SuppressWarnings("unchecked")
            public List<ClassificationLevel> doWork() throws Exception
            {
                return (List<ClassificationLevel>) attributeService.getAttribute(LEVELS_KEY);
            }
        });
    }

    /** Gets the list (in descending order) of classification levels - as defined in the system configuration. */
    List<ClassificationLevel> getConfigurationLevels()
    {
        return config.getConfiguredLevels();
    }
    
    /**
     * Gets the list of classification reasons as persisted in the system.
     * @return the list of classification reasons if they have been persisted, else {@code null}.
     */
    List<ClassificationReason> getPersistedReasons() {
        return authenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<List<ClassificationReason>>()
        {
            @Override
            @SuppressWarnings("unchecked")
            public List<ClassificationReason> doWork() throws Exception
            {
                return (List<ClassificationReason>) attributeService.getAttribute(REASONS_KEY);
            }
        });
    }

    /** Gets the list of classification reasons - as defined and ordered in the system configuration. */
    List<ClassificationReason> getConfigurationReasons()
    {
        return config.getConfiguredReasons();
    }

    @Override
    public List<ClassificationLevel> getClassificationLevels()
    {
        return configuredLevels == null ? Collections.<ClassificationLevel>emptyList() :
                                          Collections.unmodifiableList(configuredLevels);
    }
}