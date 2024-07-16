package com.example.demo;
import java.util.Objects;

public class FilterAliasesRequest {

    private String filter;
    private String password;
    private String keystoreName;

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public FilterAliasesRequest() {
    }

    public FilterAliasesRequest(String filter, String password) {
        this.filter = filter;
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FilterAliasesRequest filter(String filter) {
        setFilter(filter);
        return this;
    }

    public FilterAliasesRequest password(String password) {
        setPassword(password);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof FilterAliasesRequest)) {
            return false;
        }
        FilterAliasesRequest filterAliasesRequest = (FilterAliasesRequest) o;
        return Objects.equals(filter, filterAliasesRequest.filter) && Objects.equals(password, filterAliasesRequest.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filter, password);
    }

    @Override
    public String toString() {
        return "{" +
            " filter='" + getFilter() + "'" +
            ", password='" + getPassword() + "'" +
            "}";
    }

    public String getKeystoreName() {
        return this.keystoreName;
    }

    public void setKeystoreName(String keystoreName) {
        this.keystoreName = keystoreName;
    }
    
}
