/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
Website : http://www.gephi.org

This file is part of Gephi.

DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 2011 Gephi Consortium. All rights reserved.

The contents of this file are subject to the terms of either the GNU
General Public License Version 3 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://gephi.org/about/legal/license-notice/
or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License files at
/cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 3, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 3] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 3 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 3 code and therefore, elected the GPL
Version 3 license, then the option applies only if the new code is
made subject to such option by the copyright holder.

Contributor(s):

Portions Copyrighted 2011 Gephi Consortium.
*/
package org.gephi.desktop.clustering;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.gephi.attribute.api.AttributeModel;
import org.gephi.attribute.api.Column;
import org.gephi.attribute.api.Origin;
import org.gephi.clustering.api.Cluster;
import org.gephi.clustering.api.ClusteringController;
import org.gephi.clustering.api.ClusteringModel;
import org.gephi.clustering.spi.Clusterer;
import org.gephi.clustering.spi.ClustererBuilder;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.project.api.ProjectController;
import org.gephi.utils.longtask.api.LongTaskErrorHandler;
import org.gephi.utils.longtask.api.LongTaskExecutor;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.visualization.VizController;
import org.gephi.visualization.api.selection.SelectionManager;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = ClusteringController.class)
public class ClusteringControllerImpl implements ClusteringController {

    private LongTaskExecutor executor;
    private LongTaskErrorHandler errorHandler;

    public ClusteringControllerImpl() {
        executor = new LongTaskExecutor(true, "Clusterer", 10);
        errorHandler = new LongTaskErrorHandler() {

            public void fatalError(Throwable t) {
                Logger.getLogger("").log(Level.SEVERE, "", t.getCause() != null ? t.getCause() : t);
            }
        };
        executor.setDefaultErrorHandler(errorHandler);
    }

    public void clusterize(final Clusterer clusterer) {
        //Get Graph
        GraphController gc = Lookup.getDefault().lookup(GraphController.class);
        final GraphModel graphModel = gc.getGraphModel();

        //Model
        final ClusteringModel model = Lookup.getDefault().lookup(ProjectController.class).getCurrentWorkspace().getLookup().lookup(ClusteringModel.class);

        //LongTask
        LongTask task = null;
        if (clusterer instanceof LongTask) {
            task = (LongTask) clusterer;
        }
        executor.execute(task, new Runnable() {

            public void run() {
                model.setRunning(true);
                clusterer.execute(graphModel);
                writeColumns(clusterer);
                model.setRunning(false);
            }
        });
    }

    public void cancelClusterize(Clusterer clusterer) {
        executor.cancel();
    }

    private void writeColumns(Clusterer clusterer) {
        Cluster[] clusters = clusterer.getClusters();
        if (clusters != null && clusters.length > 0) {
            ClustererBuilder builder = getBuilder(clusterer);
            AttributeModel am = Lookup.getDefault().lookup(GraphController.class).getAttributeModel();
            String id = "clustering_" + builder.getName();
            String title = "Clustering (" + builder.getName() + ")";
            Column col = am.getNodeTable().getColumn(id);
            if (col == null) {
                col = am.getNodeTable().addColumn(id, title, Integer.class, Origin.DATA /* TODO: Better COMPUTED? */, null, true);
                StatusDisplayer.getDefault().setStatusText("A new column \"" + title + "\" has been created");
            }
            for (int i = 0; i < clusters.length; i++) {
                Integer clusterId = new Integer(i);
                for (Node n : clusters[i].getNodes()) {
                    n.setAttribute(col, clusterId);
                }
            }
        }
    }

    public void selectCluster(Cluster cluster) {
        SelectionManager selectionManager = VizController.getInstance().getSelectionManager();
        selectionManager.resetSelection();
        selectionManager.selectNodes(cluster.getNodes());
    }

    public void groupCluster(Cluster cluster) {
//         TODO really to remove?
//         GraphModel gm = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
//         if (gm != null) {
//              HierarchicalGraph graph = gm.getHierarchicalGraphVisible();
//             Node[] newGroup = cluster.getNodes();
//             float centroidX = 0;
//             float centroidY = 0;
//             int len = 0;
//             Node group = graph.groupNodes(newGroup);
//             cluster.setMetaNode(group);
// 
//             group.setLabel("Group");
//             group.setSize(10f);
//             for (Node child : newGroup) {
//                 centroidX += child.x();
//                 centroidY += child.y();
//                 len++;
//             }
//             centroidX /= len;
//             centroidY /= len;
//             group.setX(centroidX);
//             group.setY(centroidY);
//        }
    }

    public void ungroupCluster(Cluster cluster) {
        // TODO really to remove?
        // GraphModel gm = Lookup.getDefault().lookup(GraphController.class).getModel();
        // if (gm != null) {
        //    Graph graph = gm.getGraphVisible();
        //    graph.ungroupNodes(cluster.getMetaNode());
        //    cluster.setMetaNode(null);
        //}
    }

    public boolean canGroup(Cluster cluster) {
        // TODO really to remove?
        // return cluster.getMetaNode() == null;
        return false;
    }

    public boolean canUngroup(Cluster cluster) {
        // TODO really to remove?
        // return cluster.getMetaNode() != null;
        return false;
    }

    private ClustererBuilder getBuilder(Clusterer clusterer) {
        for (ClustererBuilder b : Lookup.getDefault().lookupAll(ClustererBuilder.class)) {
            if (b.getClustererClass().equals(clusterer.getClass())) {
                return b;
            }
        }
        return null;
    }
}
