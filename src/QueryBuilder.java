import java.util.ArrayList;
import java.util.List;

public class QueryBuilder {

    private String        table;
    private List<String>  conditions = new ArrayList<>();
    private List<String>  columns    = new ArrayList<>();
    private String        orderBy;
    private int           limit      = -1;
    private int           offset     = 0;
    private String        operation  = "SELECT";

    public QueryBuilder from(String table) {
        this.table = table;
        return this;
    }

    public QueryBuilder select(String... cols) {
        for (String c : cols) columns.add(c);
        this.operation = "SELECT";
        return this;
    }

    public QueryBuilder where(String condition) {
        conditions.add(condition);
        return this;
    }

    public QueryBuilder orderBy(String column) {
        this.orderBy = column;
        return this;
    }

    public QueryBuilder limit(int n) {
        this.limit = n;
        return this;
    }

    public QueryBuilder offset(int n) {
        this.offset = n;
        return this;
    }

    public String build() {
        if (table == null) throw new IllegalStateException("Table not set");
        StringBuilder sb = new StringBuilder();
        sb.append(operation).append(" ");
        sb.append(columns.isEmpty() ? "*" : String.join(", ", columns));
        sb.append(" FROM ").append(table);
        if (!conditions.isEmpty())
            sb.append(" WHERE ").append(String.join(" AND ", conditions));
        if (orderBy != null)
            sb.append(" ORDER BY ").append(orderBy);
        if (limit > 0)
            sb.append(" LIMIT ").append(limit);
        if (offset > 0)
            sb.append(" OFFSET ").append(offset);
        return sb.toString();
    }

    public void reset() {
        table      = null;
        conditions.clear();
        columns.clear();
        orderBy    = null;
        limit      = -1;
        offset     = 0;
        operation  = "SELECT";
    }
}
