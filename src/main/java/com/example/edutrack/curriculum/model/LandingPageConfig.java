package com.example.edutrack.curriculum.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Entity
public class LandingPageConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MenteeLandingRole role;

    // HERO SECTION
    @NotBlank @Size(max = 100)
    private String heroHeadline;

    @NotBlank @Size(max = 200)
    private String heroSubHeadline;

    @NotBlank @Size(max = 50)
    private String heroCTA;

    @NotBlank @Size(max = 255)
    private String heroCTALink;

    @NotBlank @Size(max = 100)
    private String categoryTitle;

    @NotBlank @Size(max = 100)
    private String categorySubtitle;

    @NotBlank @Size(max = 50)
    private String categoryButtonText;

    @NotBlank @Size(max = 1000)
    @Column(length = 1000)
    private String aboutTitle;

    @NotBlank @Size(max = 100)
    private String aboutSubtitle;

    @NotBlank @Size(max = 2000)
    @Column(length = 2000)
    private String aboutDescription;

    // COURSE SECTION ONE
    @NotBlank @Size(max = 100)
    private String sectionOneTitle;

    @NotBlank @Size(max = 100)
    private String sectionOneSubtitle;

    // COURSE SECTION TWO
    @NotBlank @Size(max = 100)
    private String sectionTwoTitle;

    @NotBlank @Size(max = 100)
    private String sectionTwoSubtitle;

    // MENTOR SECTION
    @NotBlank @Size(max = 100)
    private String mentorSectionTitle;

    @NotBlank @Size(max = 100)
    private String mentorSectionSubtitle;

    @NotBlank @Size(max = 1000)
    @Column(length = 1000)
    private String footerDescription;

    @NotBlank @Size(max = 255)
    private String copyrightText;

    @Enumerated(EnumType.STRING)
    @Column(name = "course_section_one_suggestion")
    private SuggestionType courseSectionOneSuggestion = SuggestionType.POPULAR;

    @Enumerated(EnumType.STRING)
    @Column(name = "mentor_suggestion")
    private SuggestionType mentorSuggestion = SuggestionType.TOP_RATED;

    @Enumerated(EnumType.STRING)
    @Column(name = "course_section_two_suggestion")
    private SuggestionType courseSectionTwoSuggestion = SuggestionType.LATEST;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag_suggestion")
    private SuggestionType tagSuggestion = SuggestionType.POPULAR;

    private String heroImageUrl;

    private String categorySectionBgUrl;

    private String aboutSectionImageUrl;

    private String courseSectionBgUrl;

    private String mentorSectionBgUrl;

    private boolean usePersonalization = false;
    private boolean useScheduleReminder = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MenteeLandingRole getRole() {
        return role;
    }

    public void setRole(MenteeLandingRole role) {
        this.role = role;
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

    public String getAboutSubtitle() {
        return aboutSubtitle;
    }

    public void setAboutSubtitle(String aboutSubtitle) {
        this.aboutSubtitle = aboutSubtitle;
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

    public SuggestionType getCourseSectionOneSuggestion() {
        return courseSectionOneSuggestion;
    }

    public void setCourseSectionOneSuggestion(SuggestionType courseSectionOneSuggestion) {
        this.courseSectionOneSuggestion = courseSectionOneSuggestion;
    }

    public SuggestionType getMentorSuggestion() {
        return mentorSuggestion;
    }

    public void setMentorSuggestion(SuggestionType mentorSuggestion) {
        this.mentorSuggestion = mentorSuggestion;
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

    public String getHeroImageUrl() {
        return heroImageUrl;
    }

    public void setHeroImageUrl(String heroImageUrl) {
        this.heroImageUrl = heroImageUrl;
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

    public boolean isUsePersonalization() {
        return usePersonalization;
    }

    public void setUsePersonalization(boolean usePersonalization) {
        this.usePersonalization = usePersonalization;
    }

    public boolean isUseScheduleReminder() {
        return useScheduleReminder;
    }

    public void setUseScheduleReminder(boolean useScheduleReminder) {
        this.useScheduleReminder = useScheduleReminder;
    }

    public LandingPageConfig(){}

    public LandingPageConfig(Long id, MenteeLandingRole role, String heroHeadline, String heroSubHeadline, String heroCTA, String heroCTALink, String categoryTitle, String categorySubtitle, String categoryButtonText, String aboutTitle, String aboutSubtitle, String aboutDescription, String sectionOneTitle, String sectionOneSubtitle, String sectionTwoTitle, String sectionTwoSubtitle, String mentorSectionTitle, String mentorSectionSubtitle, String footerDescription, String copyrightText, SuggestionType courseSectionOneSuggestion, SuggestionType mentorSuggestion, SuggestionType courseSectionTwoSuggestion, SuggestionType tagSuggestion, String heroImageUrl, String categorySectionBgUrl, String aboutSectionImageUrl, String courseSectionBgUrl, String mentorSectionBgUrl, boolean usePersonalization, boolean useScheduleReminder) {
        this.id = id;
        this.role = role;
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
        this.mentorSuggestion = mentorSuggestion;
        this.courseSectionTwoSuggestion = courseSectionTwoSuggestion;
        this.tagSuggestion = tagSuggestion;
        this.heroImageUrl = heroImageUrl;
        this.categorySectionBgUrl = categorySectionBgUrl;
        this.aboutSectionImageUrl = aboutSectionImageUrl;
        this.courseSectionBgUrl = courseSectionBgUrl;
        this.mentorSectionBgUrl = mentorSectionBgUrl;
        this.usePersonalization = usePersonalization;
        this.useScheduleReminder = useScheduleReminder;
    }

    public LandingPageConfig(LandingPageConfig other) {
        this.id = other.id;
        this.role = other.role;
        this.heroHeadline = other.heroHeadline;
        this.heroSubHeadline = other.heroSubHeadline;
        this.heroCTA = other.heroCTA;
        this.heroCTALink = other.heroCTALink;

        this.categoryTitle = other.categoryTitle;
        this.categorySubtitle = other.categorySubtitle;
        this.categoryButtonText = other.categoryButtonText;

        this.aboutTitle = other.aboutTitle;
        this.aboutSubtitle = other.aboutSubtitle;
        this.aboutDescription = other.aboutDescription;

        this.sectionOneTitle = other.sectionOneTitle;
        this.sectionOneSubtitle = other.sectionOneSubtitle;
        this.sectionTwoTitle = other.sectionTwoTitle;
        this.sectionTwoSubtitle = other.sectionTwoSubtitle;

        this.mentorSectionTitle = other.mentorSectionTitle;
        this.mentorSectionSubtitle = other.mentorSectionSubtitle;

        this.footerDescription = other.footerDescription;
        this.copyrightText = other.copyrightText;

        this.courseSectionOneSuggestion = other.courseSectionOneSuggestion;
        this.courseSectionTwoSuggestion = other.courseSectionTwoSuggestion;
        this.mentorSuggestion = other.mentorSuggestion;
        this.tagSuggestion = other.tagSuggestion;

        this.heroImageUrl = other.heroImageUrl;
        this.categorySectionBgUrl = other.categorySectionBgUrl;
        this.aboutSectionImageUrl = other.aboutSectionImageUrl;
        this.courseSectionBgUrl = other.courseSectionBgUrl;
        this.mentorSectionBgUrl = other.mentorSectionBgUrl;

        this.usePersonalization = other.usePersonalization;
        this.useScheduleReminder = other.useScheduleReminder;
    }

}
