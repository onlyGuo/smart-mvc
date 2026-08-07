package ink.icoding.mvc.entitys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable, framework-neutral representation of a page of application data.
 *
 * <p>The result combines a defensive copy of the current page items with total-count,
 * page-number, and page-size metadata, making it suitable for standardized API responses
 * without introducing a persistence-framework dependency.</p>
 *
 * @param <T> page item type
 */
public final class PageResult<T> {

    private final List<T> items;
    private final long total;
    private final int page;
    private final int pageSize;

    public PageResult(List<T> items, long total, int page, int pageSize) {
        this.items = items == null
                ? Collections.<T>emptyList()
                : Collections.unmodifiableList(new ArrayList<T>(items));
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
    }

    public List<T> getItems() {
        return items;
    }

    public long getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalPages() {
        return pageSize <= 0 ? 0 : (total + pageSize - 1) / pageSize;
    }
}
