package io.jenkins.plugins.dude.model;

public class PluginResults {
    private String projectName;
    private String currentBuildNumber;
    private String previousBuildNumber;
    private String numberOfFilesAnalysed;
    private String numberOfDuplicatedCodeFragments;
    private String numberOfFilesContainingDuplicateFragments;
    private String filesWithDuplicateFragments;
    private String percentageOfFilesAnalysedThatHaveDuplicateFragments;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }

    public String getCurrentBuildNumber() {
        return currentBuildNumber;
    }

    public void setCurrentBuildNumber(final String currentBuildNumber) {
        this.currentBuildNumber = currentBuildNumber;
    }

    public String getPreviousBuildNumber() {
        return previousBuildNumber;
    }

    public void setPreviousBuildNumber(final String previousBuildNumber) {
        this.previousBuildNumber = previousBuildNumber;
    }

    public String getNumberOfFilesAnalysed() {
        return numberOfFilesAnalysed;
    }

    public void setNumberOfFilesAnalysed(final String numberOfFilesAnalysed) {
        this.numberOfFilesAnalysed = numberOfFilesAnalysed;
    }

    public String getNumberOfDuplicatedCodeFragments() {
        return numberOfDuplicatedCodeFragments;
    }

    public void setNumberOfDuplicatedCodeFragments(final String numberOfDuplicatedCodeFragments) {
        this.numberOfDuplicatedCodeFragments = numberOfDuplicatedCodeFragments;
    }

    public String getNumberOfFilesContainingDuplicateFragments() {
        return numberOfFilesContainingDuplicateFragments;
    }

    public void setNumberOfFilesContainingDuplicateFragments(final String numberOfFilesContainingDuplicateFragments) {
        this.numberOfFilesContainingDuplicateFragments = numberOfFilesContainingDuplicateFragments;
    }

    public String getFilesWithDuplicateFragments() {
        return filesWithDuplicateFragments;
    }

    public void setFilesWithDuplicateFragments(final String filesWithDuplicateFragments) {
        this.filesWithDuplicateFragments = filesWithDuplicateFragments;
    }

    public String getPercentageOfFilesAnalysedThatHaveDuplicateFragments() {
        return percentageOfFilesAnalysedThatHaveDuplicateFragments;
    }

    public void setPercentageOfFilesAnalysedThatHaveDuplicateFragments(final String percentageOfFilesAnalysedThatHaveDuplicateFragments) {
        this.percentageOfFilesAnalysedThatHaveDuplicateFragments = percentageOfFilesAnalysedThatHaveDuplicateFragments;
    }
}
