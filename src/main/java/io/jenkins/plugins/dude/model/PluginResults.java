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

    // duplicationFragment analysis
    private String duplicationFragmentWithMostLOCTotalLOC;
    private String duplicationFragmentWithMostLOCActualLOC;
    private String duplicationFragmentWithMostLOCFilesCount;
    private String duplicationFragmentWithMostLOCFiles;
    private String duplicationFragmentPresentInMostFilesTotalLOC;
    private String duplicationFragmentPresentInMostFilesActualLOC;
    private String duplicationFragmentPresentInMostFilesFilesCount;
    private String duplicationFragmentPresentInMostFilesFiles;

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

    public String getDuplicationFragmentWithMostLOCTotalLOC() {
        return duplicationFragmentWithMostLOCTotalLOC;
    }

    public void setDuplicationFragmentWithMostLOCTotalLOC(final String duplicationFragmentWithMostLOCTotalLOC) {
        this.duplicationFragmentWithMostLOCTotalLOC = duplicationFragmentWithMostLOCTotalLOC;
    }

    public String getDuplicationFragmentWithMostLOCActualLOC() {
        return duplicationFragmentWithMostLOCActualLOC;
    }

    public void setDuplicationFragmentWithMostLOCActualLOC(final String duplicationFragmentWithMostLOCActualLOC) {
        this.duplicationFragmentWithMostLOCActualLOC = duplicationFragmentWithMostLOCActualLOC;
    }

    public String getDuplicationFragmentWithMostLOCFilesCount() {
        return duplicationFragmentWithMostLOCFilesCount;
    }

    public void setDuplicationFragmentWithMostLOCFilesCount(final String duplicationFragmentWithMostLOCFilesCount) {
        this.duplicationFragmentWithMostLOCFilesCount = duplicationFragmentWithMostLOCFilesCount;
    }

    public String getDuplicationFragmentWithMostLOCFiles() {
        return duplicationFragmentWithMostLOCFiles;
    }

    public void setDuplicationFragmentWithMostLOCFiles(final String duplicationFragmentWithMostLOCFiles) {
        this.duplicationFragmentWithMostLOCFiles = duplicationFragmentWithMostLOCFiles;
    }

    public String getDuplicationFragmentPresentInMostFilesTotalLOC() {
        return duplicationFragmentPresentInMostFilesTotalLOC;
    }

    public void setDuplicationFragmentPresentInMostFilesTotalLOC(final String duplicationFragmentPresentInMostFilesTotalLOC) {
        this.duplicationFragmentPresentInMostFilesTotalLOC = duplicationFragmentPresentInMostFilesTotalLOC;
    }

    public String getDuplicationFragmentPresentInMostFilesActualLOC() {
        return duplicationFragmentPresentInMostFilesActualLOC;
    }

    public void setDuplicationFragmentPresentInMostFilesActualLOC(final String duplicationFragmentPresentInMostFilesActualLOC) {
        this.duplicationFragmentPresentInMostFilesActualLOC = duplicationFragmentPresentInMostFilesActualLOC;
    }

    public String getDuplicationFragmentPresentInMostFilesFilesCount() {
        return duplicationFragmentPresentInMostFilesFilesCount;
    }

    public void setDuplicationFragmentPresentInMostFilesFilesCount(final String duplicationFragmentPresentInMostFilesFilesCount) {
        this.duplicationFragmentPresentInMostFilesFilesCount = duplicationFragmentPresentInMostFilesFilesCount;
    }

    public String getDuplicationFragmentPresentInMostFilesFiles() {
        return duplicationFragmentPresentInMostFilesFiles;
    }

    public void setDuplicationFragmentPresentInMostFilesFiles(final String duplicationFragmentPresentInMostFilesFiles) {
        this.duplicationFragmentPresentInMostFilesFiles = duplicationFragmentPresentInMostFilesFiles;
    }

    @Override
    public String toString() {
        return "PluginResults{" +
               "projectName='" + projectName + '\'' +
               ", currentBuildNumber='" + currentBuildNumber + '\'' +
               ", previousBuildNumber='" + previousBuildNumber + '\'' +
               ", numberOfFilesAnalysed='" + numberOfFilesAnalysed + '\'' +
               ", numberOfDuplicatedCodeFragments='" + numberOfDuplicatedCodeFragments + '\'' +
               ", numberOfFilesContainingDuplicateFragments='" + numberOfFilesContainingDuplicateFragments + '\'' +
               ", filesWithDuplicateFragments='" + filesWithDuplicateFragments + '\'' +
               ", percentageOfFilesAnalysedThatHaveDuplicateFragments='" + percentageOfFilesAnalysedThatHaveDuplicateFragments + '\'' +
               '}';
    }
}
