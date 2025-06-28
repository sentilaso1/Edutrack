package com.example.edutrack.curriculum.dto;

import com.example.edutrack.curriculum.model.SuggestionType;

public class LandingPageConfigDTO {
    private String heroHeadline;
    private String heroSubHeadline;
    private String heroCTA;
    private String heroCTALink;

    private String categoryTitle;
    private String categorySubtitle;
    private String categoryButtonText;

    private String aboutTitle;
    private String aboutSubtitle;
    private String aboutDescription;

    private String sectionOneTitle;
    private String sectionOneSubtitle;

    private String sectionTwoTitle;
    private String sectionTwoSubtitle;

    private String mentorSectionTitle;
    private String mentorSectionSubtitle;

    private String footerDescription;
    private String copyrightText;
    private SuggestionType courseSectionOneSuggestion;
    private SuggestionType courseSectionTwoSuggestion;
    private SuggestionType tagSuggestion;
    private SuggestionType mentorSuggestion;
    private String categorySectionBgUrl;
    private String aboutSectionImageUrl;
    private String courseSectionBgUrl;
    private String mentorSectionBgUrl;
    private String heroImageUrl;
    private boolean usePersonalization;
    private boolean useScheduleReminder;

    public LandingPageConfigDTO(String heroHeadline, String heroSubHeadline, String heroCTA, String heroCTALink, String categoryTitle, String categorySubtitle, String categoryButtonText, String aboutTitle, String aboutSubtitle, String aboutDescription, String sectionOneTitle, String sectionOneSubtitle, String sectionTwoTitle, String sectionTwoSubtitle, String mentorSectionTitle, String mentorSectionSubtitle, String footerDescription, String copyrightText, SuggestionType courseSectionOneSuggestion, SuggestionType courseSectionTwoSuggestion, SuggestionType tagSuggestion, SuggestionType mentorSuggestion, String categorySectionBgUrl, String aboutSectionImageUrl, String courseSectionBgUrl, String mentorSectionBgUrl, String heroImageUrl, boolean usePersonalization, boolean useScheduleReminder) {
        this.heroHeadline = heroHeadline;
        this.heroSubHeadline = heroSubHeadline;
        this.heroCTA = heroCTA;
        this.heroCTALink = heroCTALink;
        this.categoryTitle = categoryTitle;
        this.categorySubtitle = categorySubtitle;
        this.categoryButtonText = categoryButtonText;
        this.aboutTitle = aboutTitle;
        this.aboutSubtitle = aboutSubtitle;
        this.aboutDescription = aboutDescription;
        this.sectionOneTitle = sectionOneTitle;
        this.sectionOneSubtitle = sectionOneSubtitle;
        this.sectionTwoTitle = sectionTwoTitle;
        this.sectionTwoSubtitle = sectionTwoSubtitle;
        this.mentorSectionTitle = mentorSectionTitle;
        this.mentorSectionSubtitle = mentorSectionSubtitle;
        this.footerDescription = footerDescription;
        this.copyrightText = copyrightText;
        this.courseSectionOneSuggestion = courseSectionOneSuggestion;
        this.courseSectionTwoSuggestion = courseSectionTwoSuggestion;
        this.tagSuggestion = tagSuggestion;
        this.mentorSuggestion = mentorSuggestion;
        this.categorySectionBgUrl = categorySectionBgUrl;
        this.aboutSectionImageUrl = aboutSectionImageUrl;
        this.courseSectionBgUrl = courseSectionBgUrl;
        this.mentorSectionBgUrl = mentorSectionBgUrl;
        this.heroImageUrl = heroImageUrl;
        this.usePersonalization = usePersonalization;
        this.useScheduleReminder = useScheduleReminder;
    }

    public SuggestionType getCourseSectionOneSuggestion() {
        return courseSectionOneSuggestion;
    }

    public void setCourseSectionOneSuggestion(SuggestionType courseSectionOneSuggestion) {
        this.courseSectionOneSuggestion = courseSectionOneSuggestion;
    }

    public SuggestionType getCourseSectionTwoSuggestion() {
        return courseSectionTwoSuggestion;
    }

    public void setCourseSectionTwoSuggestion(SuggestionType courseSectionTwoSuggestion) {
        this.courseSectionTwoSuggestion = courseSectionTwoSuggestion;
    }

    public SuggestionType getTagSuggestion() {
        return tagSuggestion;
    }

    public void setTagSuggestion(SuggestionType tagSuggestion) {
        this.tagSuggestion = tagSuggestion;
    }

    public SuggestionType getMentorSuggestion() {
        return mentorSuggestion;
    }

    public String getHeroImageUrl() {
        return heroImageUrl;
    }

    public void setHeroImageUrl(String heroImageUrl) {
        this.heroImageUrl = heroImageUrl;
    }

    public void setUsePersonalization(boolean usePersonalization) {
        this.usePersonalization = usePersonalization;
    }

    public void setUseScheduleReminder(boolean useScheduleReminder) {
        this.useScheduleReminder = useScheduleReminder;
    }

    public void setMentorSuggestion(SuggestionType mentorSuggestion) {
        this.mentorSuggestion = mentorSuggestion;
    }

    public String getAboutSubtitle() {
        return aboutSubtitle;
    }

    public void setAboutSubtitle(String aboutSubtitle) {
        this.aboutSubtitle = aboutSubtitle;
    }

    public String getHeroHeadline() {
        return heroHeadline;
    }

    public void setHeroHeadline(String heroHeadline) {
        this.heroHeadline = heroHeadline;
    }

    public String getHeroSubHeadline() {
        return heroSubHeadline;
    }

    public void setHeroSubHeadline(String heroSubHeadline) {
        this.heroSubHeadline = heroSubHeadline;
    }

    public String getHeroCTA() {
        return heroCTA;
    }

    public void setHeroCTA(String heroCTA) {
        this.heroCTA = heroCTA;
    }

    public String getHeroCTALink() {
        return heroCTALink;
    }

    public void setHeroCTALink(String heroCTALink) {
        this.heroCTALink = heroCTALink;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getCategorySubtitle() {
        return categorySubtitle;
    }

    public void setCategorySubtitle(String categorySubtitle) {
        this.categorySubtitle = categorySubtitle;
    }

    public String getCategoryButtonText() {
        return categoryButtonText;
    }

    public void setCategoryButtonText(String categoryButtonText) {
        this.categoryButtonText = categoryButtonText;
    }

    public String getAboutTitle() {
        return aboutTitle;
    }

    public void setAboutTitle(String aboutTitle) {
        this.aboutTitle = aboutTitle;
    }

    public String getAboutDescription() {
        return aboutDescription;
    }

    public void setAboutDescription(String aboutDescription) {
        this.aboutDescription = aboutDescription;
    }

    public String getSectionOneTitle() {
        return sectionOneTitle;
    }

    public void setSectionOneTitle(String sectionOneTitle) {
        this.sectionOneTitle = sectionOneTitle;
    }

    public String getSectionOneSubtitle() {
        return sectionOneSubtitle;
    }

    public void setSectionOneSubtitle(String sectionOneSubtitle) {
        this.sectionOneSubtitle = sectionOneSubtitle;
    }

    public String getSectionTwoTitle() {
        return sectionTwoTitle;
    }

    public void setSectionTwoTitle(String sectionTwoTitle) {
        this.sectionTwoTitle = sectionTwoTitle;
    }

    public String getSectionTwoSubtitle() {
        return sectionTwoSubtitle;
    }

    public void setSectionTwoSubtitle(String sectionTwoSubtitle) {
        this.sectionTwoSubtitle = sectionTwoSubtitle;
    }

    public String getMentorSectionTitle() {
        return mentorSectionTitle;
    }

    public void setMentorSectionTitle(String mentorSectionTitle) {
        this.mentorSectionTitle = mentorSectionTitle;
    }

    public String getMentorSectionSubtitle() {
        return mentorSectionSubtitle;
    }

    public void setMentorSectionSubtitle(String mentorSectionSubtitle) {
        this.mentorSectionSubtitle = mentorSectionSubtitle;
    }


    public String getFooterDescription() {
        return footerDescription;
    }

    public void setFooterDescription(String footerDescription) {
        this.footerDescription = footerDescription;
    }

    public String getCopyrightText() {
        return copyrightText;
    }

    public void setCopyrightText(String copyrightText) {
        this.copyrightText = copyrightText;
    }

    public String getCategorySectionBgUrl() {
        return categorySectionBgUrl;
    }

    public void setCategorySectionBgUrl(String categorySectionBgUrl) {
        this.categorySectionBgUrl = categorySectionBgUrl;
    }

    public String getAboutSectionImageUrl() {
        return aboutSectionImageUrl;
    }

    public void setAboutSectionImageUrl(String aboutSectionImageUrl) {
        this.aboutSectionImageUrl = aboutSectionImageUrl;
    }

    public String getCourseSectionBgUrl() {
        return courseSectionBgUrl;
    }

    public void setCourseSectionBgUrl(String courseSectionBgUrl) {
        this.courseSectionBgUrl = courseSectionBgUrl;
    }

    public String getMentorSectionBgUrl() {
        return mentorSectionBgUrl;
    }

    public void setMentorSectionBgUrl(String mentorSectionBgUrl) {
        this.mentorSectionBgUrl = mentorSectionBgUrl;
    }
}
