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

package co.edu.uniquindio.chord.node;

import co.edu.uniquindio.chord.node.command.CheckPredecessorObserver;
import co.edu.uniquindio.chord.node.command.FixFingersObserver;
import co.edu.uniquindio.chord.node.command.FixSuccessorsObserver;
import co.edu.uniquindio.chord.node.command.StabilizeObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StableRingTest {
    @Mock
    private ChordNode node;
    @InjectMocks
    @Spy
    private StableRing stableRing;

    @Test
    public void run_notify(){
        doNothing().when(stableRing).notifyObservers(node);

        stableRing.run();

        verify(stableRing).notifyObservers(node);
    }

}