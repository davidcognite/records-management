/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * -
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 * -
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * -
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * -
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.module.org_alfresco_module_rm.model.rma.type;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_rm.test.util.BaseUnitTest;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.junit.Test;
import org.mockito.InjectMocks;

/**
 * Unit test for TransferContainerTypeTest
 *
 * @author Mihai Cozma
 * @since 2.4
 */
public class TransferContainerTypeUnitTest extends BaseUnitTest
{
    /** test object */
    private @InjectMocks TransferContainerType transferContainerType;

    /**
     * Having the Unfilled Record container and a folder having the aspect ASPECT_HIDDEN When adding a child association
     * between the folder and the container Then the folder type shouldn't be renamed
     */
    @Test(expected = AlfrescoRuntimeException.class)
    public void testAddContentToTransferContainerTest()
    {

        NodeRef transferContainer = createTransferContainer();

        /*
         * When adding a child association between the folder and the container
         */
        NodeRef record = generateNodeRef(ContentModel.TYPE_CONTENT);
        ChildAssociationRef childAssoc = new ChildAssociationRef(ContentModel.TYPE_CONTENT, transferContainer,
                    ContentModel.TYPE_CONTENT, record);

        transferContainerType.onCreateChildAssociation(childAssoc, true);

    }

    /**
     * Generates a record management container
     *
     * @return reference to the generated container
     */
    private NodeRef createTransferContainer()
    {
        NodeRef holdContainer = generateNodeRef(TYPE_TRANSFER, true);

        return holdContainer;
    }

}
