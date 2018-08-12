/*
 *  Chord project implement of lookup algorithm Chord
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
 *
 */

package co.edu.uniquindio.chord.node.command;

import co.edu.uniquindio.chord.node.ChordNode;
import co.edu.uniquindio.chord.node.SuccessorList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FixSuccessorsObserverTest {
    @Mock
    private SuccessorList successorList;
    @Mock
    private ChordNode chordNode;
    @InjectMocks
    private FixSuccessorsObserver fixSuccessorsObserver;

    @Test
    public void run_chordNode_checkPredeccesor() {
        when(chordNode.getSuccessorList()).thenReturn(successorList);

        fixSuccessorsObserver.update(null, chordNode);

        verify(successorList).fixSuccessors();
    }
}