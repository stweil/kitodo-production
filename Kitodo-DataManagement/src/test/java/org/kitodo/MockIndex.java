/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.kitodo;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.opensearch.common.settings.Settings;
import org.opensearch.env.Environment;
import org.opensearch.node.Node;
import org.opensearch.transport.Netty4Plugin;

public class MockIndex {

    private static Node node;
    private static final String HTTP_TRANSPORT_PORT = "9305";
    private static final String TARGET = "target";

    public static void startNode() throws Exception {
        final String nodeName = "index";
        final String port = "9205"; // defined in test resources file hibernate.cfg.xml
        // path.data is "target", so the node stores its cluster state under
        // "target/nodes", not "target/index". Stale state (e.g. leftover cluster
        // blocks) from a previous run must be removed or it leaks into the next one.
        removeOldDataDirectories(new File(TARGET, "nodes").getAbsolutePath());
        Environment environment = prepareEnvironment(port, nodeName, Paths.get("target", "classes"));
        node = new ExtendedNode(environment, Collections.singleton(Netty4Plugin.class));
        node.start();
    }

    public static void stopNode() throws Exception {
        node.close();
        node = null;
    }

    private static void removeOldDataDirectories(String dataDirectory) throws Exception {
        File dataDir = new File(dataDirectory);
        if (dataDir.exists()) {
            FileUtils.deleteDirectory(dataDir);
        }
    }

    private static Environment prepareEnvironment(String httpPort, String nodeName, Path configPath) {
        Settings settings = Settings.builder().put("node.name", nodeName)
                .put("path.data", TARGET)
                .put("path.logs", TARGET)
                .put("path.home", TARGET)
                .put("http.type", "netty4")
                .put("http.port", httpPort)
                .put("transport.tcp.port", HTTP_TRANSPORT_PORT)
                .put("transport.type", "netty4")
                .put("action.auto_create_index", "false").build();
        return new Environment(settings, configPath);
    }
}
