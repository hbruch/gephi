/*
Copyright 2008-2011 Gephi
Authors : Antonio Patriarca <antoniopatriarca@gmail.com>
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.gephi.visualization.model;

import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeData;
import org.gephi.math.linalg.Vec3;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.project.api.WorkspaceListener;
import org.gephi.visualization.api.controller.MotionManager;
import org.gephi.visualization.api.selection.SelectionManager;
import org.gephi.visualization.api.selection.Shape;
import org.gephi.visualization.controller.VisualizationControllerImpl;
import org.gephi.visualization.data.FrameDataBridgeIn;
import org.gephi.visualization.api.geometry.AABB;
import org.gephi.visualization.model.styler.BasicEdgeStyler;
import org.gephi.visualization.model.styler.BasicNodeStyler;
import org.openide.util.Lookup;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class Model implements Runnable, WorkspaceListener {

    private Thread thread;
    private boolean isRunning;
    private int frameDuration;

    final private VisualizationControllerImpl controller;
    final private FrameDataBridgeIn bridge;

    private boolean workspaceSelected;
    private GraphModel graphModel;
    final private Object graphModelLock;

    public Model(VisualizationControllerImpl controller, FrameDataBridgeIn bridge, int frameDuration) {
        this.thread = null;
        this.frameDuration = frameDuration;

        this.isRunning = false;

        this.workspaceSelected = false;
        this.graphModel = null;
        this.graphModelLock = new Object();

        this.controller = controller;
        this.bridge = bridge;

        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.addWorkspaceListener(this);
    }

    public void start() {
        this.isRunning = true;

        startUpdate();
    }

    public void stop() {
        this.isRunning = false;

        stopUpdate();
    }

    @Override
    public void run() {

        while (true) {
            long beginFrameTime = System.currentTimeMillis();

            this.controller.beginUpdateFrame();
            
            this.bridge.beginFrame(this.controller.getCameraCopy());

            this.bridge.setStylers(new BasicNodeStyler(), new BasicEdgeStyler());
            
            final Graph graph;
            synchronized(this.graphModelLock) {
                graph = this.graphModel.getGraph();
            }

            AABB box = null;
            if (this.controller.isCentering()) {
                if (graph != null) {
                    for (Node n : graph.getNodes()) {
                        NodeData nd = n.getNodeData();
                        Vec3 p = new Vec3(nd.x(), nd.y(), nd.z());
                        Vec3 s = new Vec3(nd.getSize(), nd.getSize(), nd.getSize());
                        if (box == null) {
                            box = new AABB(p, s);
                        } else {
                            box.addPoint(p, s);
                        }
                    }
                }
            }

            for (Node n : graph.getNodes()) {
                this.bridge.add(n);
            }

            Shape selectionShape = Lookup.getDefault().lookup(MotionManager.class).getSelectionShape();
            if (selectionShape != null) {
                this.bridge.add(selectionShape.getUIPrimitive());
            }

            Shape pointerShape = Lookup.getDefault().lookup(SelectionManager.class).getNodePointerShape();
            if (pointerShape != null) {
                this.bridge.add(pointerShape.getUIPrimitive());
            }

            this.controller.endUpdateFrame(box);
            this.bridge.endFrame();

            long endFrameTime = System.currentTimeMillis();

            long time = beginFrameTime - endFrameTime;
            if (time < this.frameDuration) {
                try {
                    Thread.sleep(this.frameDuration - time);
		} catch (InterruptedException e) {
                    return;
		}
            }
        }
    }

    @Override
    public void initialize(Workspace workspace) {
        // Empty block
    }

    @Override
    public void select(Workspace workspace) {
        GraphController gc = Lookup.getDefault().lookup(GraphController.class);

        synchronized(this.graphModelLock) {
            this.workspaceSelected = true;
            this.graphModel = gc.getModel(workspace);
        }
        
        startUpdate();
    }

    @Override
    public void unselect(Workspace workspace) {
        synchronized(this.graphModelLock) {
            this.workspaceSelected = false;
            this.graphModel = null;
        }

        stopUpdate();
    }

    @Override
    public void close(Workspace workspace) {
        // Empty block
    }

    @Override
    public void disable() {
        // Empty block
    }

    private void startUpdate() {
        if (this.isRunning && this.workspaceSelected) {
            this.thread = new Thread(this);
            this.thread.start();
        }
    }

    private void stopUpdate() {
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
        }
    }

}