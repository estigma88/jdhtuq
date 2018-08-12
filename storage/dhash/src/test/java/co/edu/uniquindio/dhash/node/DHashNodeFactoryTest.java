/*
 *  DHash project implement a storage management
 *  Copyright (C) 2010 - 2018  Daniel Pelaez
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package co.edu.uniquindio.dhash.node;

import co.edu.uniquindio.dhash.resource.checksum.ChecksumCalculator;
import co.edu.uniquindio.dhash.resource.manager.ResourceManager;
import co.edu.uniquindio.dhash.resource.manager.ResourceManagerFactory;
import co.edu.uniquindio.dhash.resource.serialization.SerializationHandler;
import co.edu.uniquindio.overlay.*;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.StorageNode;
import co.edu.uniquindio.utils.communication.message.SequenceGenerator;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DHashNodeFactoryTest {
    @Mock
    private CommunicationManager communicationManager;
    @Mock
    private OverlayNodeFactory overlayNodeFactory;
    @Mock
    private SerializationHandler serializationHandler;
    @Mock
    private ChecksumCalculator checksumeCalculator;
    @Mock
    private ResourceManagerFactory resourceManagerFactory;
    @Mock
    private ResourceManager resourceManager;
    @Mock
    private KeyFactory keyFactory;
    @Mock
    private SequenceGenerator sequenceGenerator;
    @Mock
    private Key key;
    @Mock
    private OverlayNode overlayNode;
    @Mock
    private DHashNode dhashNode;
    @Mock
    private DHashEnvironment dHashEnviroment;
    private DHashNodeFactory dHashNodeFactory;
    @Mock
    private Observable observable;
    @Mock
    private ReAssignObserver reAssignObserver;

    @Before
    public void before() {
        dHashNodeFactory = spy(new DHashNodeFactory(communicationManager, overlayNodeFactory, serializationHandler, checksumeCalculator, resourceManagerFactory, 2, keyFactory, sequenceGenerator));
    }

    @Test
    public void create_byName_nodeCreated() throws OverlayException, DHashFactoryException {
        when(overlayNodeFactory.createNode("node")).thenReturn(overlayNode);
        when(overlayNode.getObservable()).thenReturn(observable);
        when(resourceManagerFactory.of("node")).thenReturn(resourceManager);
        doReturn(dhashNode).when(dHashNodeFactory).getDhashNode("node", overlayNode, resourceManager);
        doReturn(dHashEnviroment).when(dHashNodeFactory).getDHashEnviroment(dhashNode, resourceManager);
        doReturn(reAssignObserver).when(dHashNodeFactory).getReAssignObserver(dhashNode);

        StorageNode node = dHashNodeFactory.createNode("node");

        assertThat(node).isEqualTo(dhashNode);

        verify(observable).addObserver(reAssignObserver);
        verify(communicationManager).addMessageProcessor("node", dHashEnviroment);
    }

    @Test
    public void destroyNode_nodeDestroyed() throws OverlayException, StorageException {
        dHashNodeFactory.destroyNode(dhashNode);

        verify(dhashNode).leave();
    }
}