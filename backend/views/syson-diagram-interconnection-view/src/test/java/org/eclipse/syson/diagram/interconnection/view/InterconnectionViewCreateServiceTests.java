/*******************************************************************************
 * Copyright (c) 2024, 2025 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.syson.diagram.interconnection.view;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.Optional;

import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IFeedbackMessageService;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.view.diagram.DiagramDescription;
import org.eclipse.sirius.components.view.diagram.EdgeDescription;
import org.eclipse.sirius.components.view.diagram.NodeDescription;
import org.eclipse.sirius.components.view.emf.diagram.api.IViewDiagramDescriptionSearchService;
import org.eclipse.syson.diagram.common.view.services.ShowDiagramsInheritedMembersService;
import org.eclipse.syson.diagram.interconnection.view.services.InterconnectionViewCreateService;
import org.eclipse.syson.sysml.BindingConnectorAsUsage;
import org.eclipse.syson.sysml.FeatureMembership;
import org.eclipse.syson.sysml.FlowUsage;
import org.eclipse.syson.sysml.InterfaceUsage;
import org.eclipse.syson.sysml.Namespace;
import org.eclipse.syson.sysml.OwningMembership;
import org.eclipse.syson.sysml.Package;
import org.eclipse.syson.sysml.PartDefinition;
import org.eclipse.syson.sysml.PartUsage;
import org.eclipse.syson.sysml.PortUsage;
import org.eclipse.syson.sysml.util.ModelBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link InterconnectionViewCreateService}.
 *
 * @author arichard
 */
public class InterconnectionViewCreateServiceTests {

    private InterconnectionViewCreateService interconnectionViewCreateService;

    private ModelBuilder builder;

    private Package rootPkg;

    private PartDefinition partDef1;

    private PartUsage part1;

    private PartUsage part2;

    private PartUsage part3;

    private PortUsage port2;

    private PortUsage port3;

    @BeforeEach
    public void setUp() {
        var viewDiagramDescriptionSearchService = new IViewDiagramDescriptionSearchService() {

            @Override
            public Optional<NodeDescription> findViewNodeDescriptionById(IEditingContext editingContext, String edgeDescriptionId) {
                return Optional.empty();
            }

            @Override
            public Optional<EdgeDescription> findViewEdgeDescriptionById(IEditingContext editingContext, String edgeDescriptionId) {
                return Optional.empty();
            }

            @Override
            public Optional<DiagramDescription> findById(IEditingContext editingContext, String diagramDescriptionId) {
                return Optional.empty();
            }
        };
        var objectSearchService = new IObjectSearchService.NoOp();
        var showDiagramsInheritedMembersService = new ShowDiagramsInheritedMembersService();
        this.interconnectionViewCreateService = new InterconnectionViewCreateService(viewDiagramDescriptionSearchService, objectSearchService, showDiagramsInheritedMembersService,
                new IFeedbackMessageService.NoOp());
        this.builder = new ModelBuilder();
        this.build();
    }

    @DisplayName("GIVEN an existing model, WHEN the createBindingConnectorAsUsage service is called, THEN a BindingConnectorAsUsage is created under the common ancestor of the source and target.")
    @Test
    public void testCreateBindingConnectorAsUsage() {
        BindingConnectorAsUsage bindingConnectorAsUsage = this.interconnectionViewCreateService.createBindingConnectorAsUsage(this.port2, this.port3);
        assertNotNull(bindingConnectorAsUsage);
        assertSame(this.part1, bindingConnectorAsUsage.getOwningType());
        var owningMembership = bindingConnectorAsUsage.getOwningMembership();
        assertInstanceOf(FeatureMembership.class, owningMembership);
    }

    @DisplayName("GIVEN an existing model, WHEN the createInterfaceUsage service is called, THEN an InterfaceUsage is created under the root Package and an OwningMembership.")
    @Test
    public void testCreateInterfaceUsage() {
        InterfaceUsage interfaceUsage = this.interconnectionViewCreateService.createInterfaceUsage(this.port2, this.port3);
        assertNotNull(interfaceUsage);
        var owningNamespace = interfaceUsage.getOwningNamespace();
        assertSame(this.partDef1, owningNamespace);
        var owningMembership = interfaceUsage.getOwningMembership();
        assertInstanceOf(OwningMembership.class, owningMembership);
    }

    @DisplayName("GIVEN an existing model, WHEN the createFlowUsage service is called, THEN an FlowUsage is created under the root Package and an OwningMembership.")
    @Test
    public void testCreateFlowUsage() {
        FlowUsage flowUsage = this.interconnectionViewCreateService.createFlowUsage(this.port2, this.port3);
        assertNotNull(flowUsage);
        var owningNamespace = flowUsage.getOwningNamespace();
        assertSame(this.partDef1, owningNamespace);
        var owningMembership = flowUsage.getOwningMembership();
        assertInstanceOf(OwningMembership.class, owningMembership);
    }

    private void build() {
        Namespace rootNamespace = this.builder.createRootNamespace();
        this.rootPkg = this.builder.createInWithName(org.eclipse.syson.sysml.Package.class, rootNamespace, "RootPkg");
        this.partDef1 = this.builder.createInWithName(PartDefinition.class, this.rootPkg, "PartDefinition1");
        this.part1 = this.builder.createInWithName(PartUsage.class, this.partDef1, "part1");
        this.part2 = this.builder.createInWithName(PartUsage.class, this.part1, "part2");
        this.port2 = this.builder.createInWithName(PortUsage.class, this.part2, "port2");
        this.part3 = this.builder.createInWithName(PartUsage.class, this.part1, "part3");
        this.port3 = this.builder.createInWithName(PortUsage.class, this.part3, "port3");
    }
}
