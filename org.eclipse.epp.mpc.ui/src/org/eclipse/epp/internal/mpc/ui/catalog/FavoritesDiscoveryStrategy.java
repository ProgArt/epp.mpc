/*******************************************************************************
 * Copyright (c) 2010 The Eclipse Foundation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Eclipse Foundation - initial API and implementation
 *******************************************************************************/
package org.eclipse.epp.internal.mpc.ui.catalog;

import java.util.Collections;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.epp.internal.mpc.ui.catalog.MarketplaceCategory.Contents;
import org.eclipse.epp.internal.mpc.ui.catalog.UserActionCatalogItem.UserAction;
import org.eclipse.epp.mpc.core.model.ISearchResult;
import org.eclipse.epp.mpc.ui.CatalogDescriptor;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogCategory;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogItem;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;

public class FavoritesDiscoveryStrategy extends MarketplaceDiscoveryStrategy {

	private String favoritesReference;

	public FavoritesDiscoveryStrategy(CatalogDescriptor catalogDescriptor) {
		super(catalogDescriptor);
	}

	public FavoritesDiscoveryStrategy(MarketplaceDiscoveryStrategy marketplaceDiscoveryStrategy) {
		super(marketplaceDiscoveryStrategy.catalogDescriptor);
	}

	public void setFavoritesReference(String favoritesReference) {
		this.favoritesReference = favoritesReference;
	}

	public String getFavoritesReference() {
		return favoritesReference;
	}

	@Override
	public void maybeAddCatalogItem(MarketplaceCategory catalogCategory) {
		//do nothing
	}

	@Override
	protected Map<String, IInstallableUnit> computeInstalledIUs(IProgressMonitor monitor) {
		return Collections.emptyMap();
	}

	@Override
	protected void handleDiscoveryCategory(MarketplaceCategory catalogCategory) {
		catalogCategory.setContents(Contents.USER_FAVORITES);
	}

	@Override
	protected ISearchResult doPerformDiscovery(IProgressMonitor monitor) throws CoreException {
		if (favoritesReference == null) {
			return null;
		}
		return marketplaceService.userFavorites(favoritesReference, monitor);
	}

	@Override
	protected void handleSearchResult(MarketplaceCategory catalogCategory, ISearchResult result,
			IProgressMonitor monitor) {
		if (result == null) {
			addInstructionInfoItem(catalogCategory);
		} else if (result.getNodes().isEmpty()) {
			addEmptyInfoItem(catalogCategory);
		} else {
			super.handleSearchResult(catalogCategory, result, monitor);
			for (CatalogItem catalogItem : items) {
				if (catalogItem instanceof MarketplaceNodeCatalogItem) {
					catalogItem.setSelected(true);
				}
			}
		}
	}

	private void addEmptyInfoItem(CatalogCategory catalogCategory) {
		addInfoItem(catalogCategory, Messages.FavoritesDiscoveryStrategy_noFavoritesMessage);
	}

	private void addInstructionInfoItem(CatalogCategory catalogCategory) {
		addInfoItem(catalogCategory,
				Messages.FavoritesDiscoveryStrategy_enterUserIdOrMailMessage);
	}

	private void addInfoItem(CatalogCategory catalogCategory, String label) {
		UserActionCatalogItem infoItem = new UserActionCatalogItem();
		infoItem.setUserAction(UserAction.INFO);
		infoItem.setDescription(label);
		infoItem.setSource(getCatalogSource());
		infoItem.setId(catalogDescriptor.getUrl().toString() + "#info:" + label); //$NON-NLS-1$
		infoItem.setCategoryId(catalogCategory.getId());
		items.add(infoItem);
	}
}