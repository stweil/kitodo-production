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

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import org.opensearch.Version;
import org.opensearch.env.Environment;
import org.opensearch.node.Node;
import org.opensearch.plugins.Plugin;
import org.opensearch.plugins.PluginInfo;

public class ExtendedNode extends Node {
    ExtendedNode(Environment initialEnvironment, Collection<Class<? extends Plugin>> classpathPlugins) {
        super(initialEnvironment, toPluginInfos(classpathPlugins), true);
    }

    private static Collection<PluginInfo> toPluginInfos(Collection<Class<? extends Plugin>> classpathPlugins) {
        return classpathPlugins.stream().map(clazz ->
                new PluginInfo(clazz.getSimpleName(), clazz.getSimpleName(), Version.CURRENT.toString(),
                        Version.CURRENT, "21", clazz.getName(), Collections.emptyList(), false)).collect(Collectors.toList());
    }
}
