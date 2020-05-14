package ap.categories.filter;

import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.publisher.web.internal.util.AssetPublisherCustomizer;
import com.liferay.asset.util.AssetEntryQueryProcessor;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author marcialcalvo
 */
@Component(immediate = true, property = { "service.ranking:Integer=100" }, service = AssetPublisherCustomizer.class)
public class MyAssetPublisherCustomizer implements AssetPublisherCustomizer {

	@Override
	public Integer getDelta(HttpServletRequest httpServletRequest) {
		return _defaultAssetPublisherCustomizer.getDelta(httpServletRequest);
	}

	@Override
	public String getPortletId() {
		return _defaultAssetPublisherCustomizer.getPortletId();
	}

	@Override
	public boolean isEnablePermissions(HttpServletRequest httpServletRequest) {
		return _defaultAssetPublisherCustomizer.isEnablePermissions(httpServletRequest);
	}

	@Override
	public boolean isOrderingAndGroupingEnabled(HttpServletRequest httpServletRequest) {
		return _defaultAssetPublisherCustomizer.isOrderingAndGroupingEnabled(httpServletRequest);
	}

	@Override
	public boolean isOrderingByTitleEnabled(HttpServletRequest httpServletRequest) {
		return _defaultAssetPublisherCustomizer.isOrderingByTitleEnabled(httpServletRequest);
	}

	@Override
	public boolean isSelectionStyleEnabled(HttpServletRequest httpServletRequest) {
		return _defaultAssetPublisherCustomizer.isSelectionStyleEnabled(httpServletRequest);
	}

	@Override
	public boolean isShowAssetEntryQueryProcessor(AssetEntryQueryProcessor assetEntryQueryProcessor) {
		return _defaultAssetPublisherCustomizer.isShowAssetEntryQueryProcessor(assetEntryQueryProcessor);
	}

	@Override
	public boolean isShowEnableAddContentButton(HttpServletRequest httpServletRequest) {
		return _defaultAssetPublisherCustomizer.isShowEnableAddContentButton(httpServletRequest);
	}

	@Override
	public boolean isShowEnableRelatedAssets(HttpServletRequest httpServletRequest) {
		return _defaultAssetPublisherCustomizer.isShowEnableRelatedAssets(httpServletRequest);
	}

	@Override
	public boolean isShowScopeSelector(HttpServletRequest httpServletRequest) {
		return _defaultAssetPublisherCustomizer.isShowScopeSelector(httpServletRequest);
	}

	@Override
	public boolean isShowSubtypeFieldsFilter(HttpServletRequest httpServletRequest) {
		return _defaultAssetPublisherCustomizer.isShowSubtypeFieldsFilter(httpServletRequest);
	}

	@Override
	public void setAssetEntryQueryOptions(AssetEntryQuery assetEntryQuery, HttpServletRequest httpServletRequest) {
		_defaultAssetPublisherCustomizer.setAssetEntryQueryOptions(assetEntryQuery, httpServletRequest);

		setAssetCategoryIdsFromRequest(assetEntryQuery, httpServletRequest);

		setKeywordsFromRequest(assetEntryQuery, httpServletRequest);
	}

	protected void setAssetCategoryIdsFromRequest(AssetEntryQuery assetEntryQuery,
			HttpServletRequest httpServletRequest) {
		long[] categoryIds = ParamUtil.getLongValues(PortalUtil.getOriginalServletRequest(httpServletRequest),
				p_categoryIds);

		if (categoryIds.length > 0) {
			assetEntryQuery.setAllCategoryIds(categoryIds);
		}
	}

	protected void setKeywordsFromRequest(AssetEntryQuery assetEntryQuery, HttpServletRequest httpServletRequest) {
		String keywords = ParamUtil.getString(PortalUtil.getOriginalServletRequest(httpServletRequest), p_keywords);

		if (Validator.isNotNull(keywords)) {
			assetEntryQuery.setKeywords(keywords);
		}
	}

	@Reference(target = "(component.name=com.liferay.asset.publisher.web.internal.util.DefaultAssetPublisherCustomizer)")
	private AssetPublisherCustomizer _defaultAssetPublisherCustomizer;

	private static final String p_categoryIds = "categoryIds";

	private static final String p_keywords = "keywords";

}