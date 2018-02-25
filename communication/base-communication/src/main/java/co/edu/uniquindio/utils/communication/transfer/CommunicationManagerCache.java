/*
 *  Communication project implement communication point to point and multicast
 *  Copyright (C) 2010  Daniel Pelaez, Daniel Lopez, Hector Hurtado
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

package co.edu.uniquindio.utils.communication.transfer;

import co.edu.uniquindio.utils.communication.configurations.CommunicationProperties;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code ComunicationManagerCache} class is used to create
 * CommunicationManagerWaitingResult and have centralized access these instances
 *
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0.2, 17/06/2010
 * @see CommunicationManagerWaitingResult
 * @see CommunicationManager
 * @since 1.0.2
 */
public class CommunicationManagerCache {

    /**
     * Logger
     */
    private static final Logger logger = Logger
            .getLogger(CommunicationManagerCache.class);

    /**
     * Map of instances of communication manager
     */
    private static Map<String, CommunicationManager> communicationManagers;

    /**
     * Builds an CommunicationManagerCache
     */
    private CommunicationManagerCache() {
        communicationManagers = new HashMap<String, CommunicationManager>();
    }

    /**
     * Gets the single instance of the communication manager.
     */

    public static CommunicationManager createCommunicationManager(
            String instanceName, CommunicationProperties communicationProperties) {

        if (communicationManagers == null) {
            communicationManagers = new HashMap<String, CommunicationManager>();
        }
        if (communicationManagers.containsKey(instanceName)) {

            communicationProperties.getParams().put(
                    "RESPONSE_TIME",
                    String.valueOf(communicationProperties.getTime()
                            .getWaitingResult()));

            communicationManagers.get(instanceName).setCommunicationProperties(
                    communicationProperties.getParams());

            logger.warn("Changed properties of '" + instanceName + "'");

            return communicationManagers.get(instanceName);
        }

        CommunicationManagerWaitingResult communicationManagerWaitingResult = null;
        Class<?> classIn;

        try {

            classIn = Class.forName(communicationProperties.getInstance()
                    .getClazz());
            communicationManagerWaitingResult = (CommunicationManagerWaitingResult) classIn
                    .newInstance();

            communicationProperties.getParams().put(
                    "RESPONSE_TIME",
                    String.valueOf(communicationProperties.getTime()
                            .getWaitingResult()));

            communicationManagerWaitingResult
                    .setCommunicationProperties(communicationProperties
                            .getParams());

            communicationManagerWaitingResult.init();

            communicationManagers.put(instanceName,
                    communicationManagerWaitingResult);

        } catch (ClassNotFoundException e) {
            CommunicationManagerException exception = new CommunicationManagerException(
                    "Class not bound ", e);
            logger.fatal("Class not bound ", exception);
        } catch (InstantiationException e) {
            CommunicationManagerException exception = new CommunicationManagerException(
                    "Error instantiating class ", e);
            logger.fatal("Error instantiating class", exception);
        } catch (IllegalAccessException e) {
            CommunicationManagerException exception = new CommunicationManagerException(
                    "Permission error ", e);
            logger.fatal("Permission error ", exception);
        } catch (ClassCastException e) {
            CommunicationManagerException exception = new CommunicationManagerException(
                    "The class '"
                            + communicationProperties.getInstance().getClazz()
                            + "' is not instance of "
                            + CommunicationManager.class.getName(), e);
            logger.fatal(e.getMessage(), exception);
        }

        return communicationManagerWaitingResult;

    }

    /**
     * Gets instance of CommunicationManager by instance name
     *
     * @param instanceName Instance name
     * @return CommunicationManager instance
     */
    public static CommunicationManager getCommunicationManager(
            String instanceName) {

        return communicationManagers.get(instanceName);
    }
}
