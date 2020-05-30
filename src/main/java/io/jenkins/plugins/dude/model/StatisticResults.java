package io.jenkins.plugins.dude.model;

import java.util.Set;

public class StatisticResults {
    private Integer numberOfFilesAnalysed;
    private Integer numberOfDuplicatedCodeFragments;
    private Integer numberOfFilesContainingDuplicateFragments;
    private Set<String> filesWithDuplicateFragments;
    private Double percentageOfFilesAnalysedThatHaveDuplicateFragments;

    public Integer getNumberOfFilesAnalysed() {
        return numberOfFilesAnalysed;
    }

    public void setNumberOfFilesAnalysed(final Integer numberOfFilesAnalysed) {
        this.numberOfFilesAnalysed = numberOfFilesAnalysed;
    }

    public Integer getNumberOfDuplicatedCodeFragments() {
        return numberOfDuplicatedCodeFragments;
    }

    public void setNumberOfDuplicatedCodeFragments(final Integer numberOfDuplicatedCodeFragments) {
        this.numberOfDuplicatedCodeFragments = numberOfDuplicatedCodeFragments;
    }

    public Integer getNumberOfFilesContainingDuplicateFragments() {
        return numberOfFilesContainingDuplicateFragments;
    }

    public void setNumberOfFilesContainingDuplicateFragments(final Integer numberOfFilesContainingDuplicateFragments) {
        this.numberOfFilesContainingDuplicateFragments = numberOfFilesContainingDuplicateFragments;
    }

    public Set<String> getFilesWithDuplicateFragments() {
        return filesWithDuplicateFragments;
    }

    public void setFilesWithDuplicateFragments(final Set<String> filesWithDuplicateFragments) {
        this.filesWithDuplicateFragments = filesWithDuplicateFragments;
    }

    public Double getPercentageOfFilesAnalysedThatHaveDuplicateFragments() {
        return percentageOfFilesAnalysedThatHaveDuplicateFragments;
    }

    public void setPercentageOfFilesAnalysedThatHaveDuplicateFragments(final Double percentageOfFilesAnalysedThatHaveDuplicateFragments) {
        this.percentageOfFilesAnalysedThatHaveDuplicateFragments = percentageOfFilesAnalysedThatHaveDuplicateFragments;
    }
}