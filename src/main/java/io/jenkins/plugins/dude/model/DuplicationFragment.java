package io.jenkins.plugins.dude.model;

import java.util.Set;

public class DuplicationFragment {
    private String duplicationFragment;
    private Integer duplicationTotalLOC;
    private Integer duplicationActualLOC;
    private Integer filesHavingThisDuplicationFragmentCount;
    private Set<String> filesHavingThisDuplicationFragment;

    public String getDuplicationFragment() {
        return duplicationFragment;
    }

    public void setDuplicationFragment(final String duplicationFragment) {
        this.duplicationFragment = duplicationFragment;
    }

    public Integer getDuplicationTotalLOC() {
        return duplicationTotalLOC;
    }

    public void setDuplicationTotalLOC(final Integer duplicationTotalLOC) {
        this.duplicationTotalLOC = duplicationTotalLOC;
    }

    public Integer getDuplicationActualLOC() {
        return duplicationActualLOC;
    }

    public void setDuplicationActualLOC(final Integer duplicationActualLOC) {
        this.duplicationActualLOC = duplicationActualLOC;
    }

    public Integer getFilesHavingThisDuplicationFragmentCount() {
        return filesHavingThisDuplicationFragmentCount;
    }

    public void setFilesHavingThisDuplicationFragmentCount(final Integer filesHavingThisDuplicationFragmentCount) {
        this.filesHavingThisDuplicationFragmentCount = filesHavingThisDuplicationFragmentCount;
    }

    public Set<String> getFilesHavingThisDuplicationFragment() {
        return filesHavingThisDuplicationFragment;
    }

    public void setFilesHavingThisDuplicationFragment(final Set<String> filesHavingThisDuplicationFragment) {
        this.filesHavingThisDuplicationFragment = filesHavingThisDuplicationFragment;
    }
}
