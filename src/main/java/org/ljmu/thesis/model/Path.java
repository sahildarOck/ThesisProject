package org.ljmu.thesis.model;

public class Path {
    private String pmdBin;
    private String cropDataset;
    private String gitRepos;
    private String metadata;
    private String codeSmellRules;
    private String outputDirectory;

    public String getPmdBin() {
        return pmdBin;
    }

    public void setPmdBin(String pmdBin) {
        this.pmdBin = pmdBin;
    }

    public String getCropDataset() {
        return cropDataset;
    }

    public void setCropDataset(String cropDataset) {
        this.cropDataset = cropDataset;
    }

    public String getGitRepos() {
        return gitRepos;
    }

    public void setGitRepos(String gitRepos) {
        this.gitRepos = gitRepos;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getCodeSmellRules() {
        return codeSmellRules;
    }

    public void setCodeSmellRules(String codeSmellRules) {
        this.codeSmellRules = codeSmellRules;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
}
