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

import co.edu.uniquindio.overlay.Key;
import co.edu.uniquindio.overlay.KeyFactory;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReAssignObserverTest {
    @Mock
    private DHashNode dhashNode;
    @Mock
    private KeyFactory keyFactory;
    @Captor
    private ArgumentCaptor<Key> keyCaptor;
    @InjectMocks
    private ReAssignObserver reAssignObserver;
    @Mock
    private Key key;

    @Test
    public void update_messageLength1_doNothing() {
        String[] message = new String[]{"REASSIGN"};

        reAssignObserver.update(null, message);

        verifyZeroInteractions(dhashNode);
    }

    @Test
    public void update_messageLength3_doNothing() {
        String[] message = new String[]{"REASSIGN", "REASSIGN", "REASSIGN"};

        reAssignObserver.update(null, message);

        verifyZeroInteractions(dhashNode);
    }

    @Test
    public void update_messageTypeWrong_doNothing() {
        Message message = Message.builder()
                .messageType(MessageType.builder().name("WRONG").build())
                .build();
        reAssignObserver.update(null, message);

        verifyZeroInteractions(dhashNode);
    }

    @Test
    public void update_messageCorrect_relocateAllResources() throws StorageException {
        Message message = Message.builder()
                .messageType(MessageType.builder().name("RE_ASSIGN").build())
                .param("PREDECESSOR", "123")
                .build();

        when(keyFactory.newKey("123")).thenReturn(key);

        reAssignObserver.update(null, message);

        verify(dhashNode).relocateAllResources(key);
    }
}