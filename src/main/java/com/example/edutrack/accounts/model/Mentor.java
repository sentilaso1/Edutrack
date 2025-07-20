    package com.example.edutrack.accounts.model;

    import com.example.edutrack.curriculum.model.Course;
    import com.example.edutrack.curriculum.model.CourseMentor;
    import jakarta.persistence.*;

    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.List;
    import java.util.stream.Collectors;

    @Entity
    @Table(name = "mentors")
    @PrimaryKeyJoinColumn(name = "user_id")
    public class Mentor extends User {

        public static final String ITEM_SEPARATOR_REGEX = "[,;]+";

        @Column(name = "is_available")
        private Boolean isAvailable = Boolean.FALSE;

        @Column(name = "total_sessions")
        private Integer totalSessions = 0;

        @Column(length = 512)
        private String expertise;

        @Column(columnDefinition = "DECIMAL(2,1)")
        private Double rating;

        public Mentor() {
            super();
        }

        public Mentor(User user) {
            super(user.getEmail(), user.getPassword(), user.getFullName(), user.getPhone(), user.getBirthDate(), user.getGender());
        }

        public boolean isAvailable() {
            return isAvailable;
        }
        public void setAvailable(boolean available) {
            isAvailable = available;
        }

        public Integer getTotalSessions() {
            return totalSessions;
        }

        public void setTotalSessions(Integer totalSessions) {
            this.totalSessions = totalSessions;
        }

        public String getExpertise() {
            return expertise;
        }

        public void setExpertise(String expertise) {
            this.expertise = expertise;
        }

        public Double getRating() {
            return rating;
        }

        public void setRating(Double rating) {
            this.rating = rating;
        }

        private static List<String> getItemList(String itemString) {
            if (itemString == null || itemString.isEmpty()) {
                return List.of();
            }

            return Arrays.stream(itemString.split(ITEM_SEPARATOR_REGEX))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        public List<String> getExpertiseItem() {
            return getItemList(getExpertise());
        }

        @OneToMany(mappedBy = "mentor")
        private List<CourseMentor> applications;

        public List<CourseMentor> getApplications() {
            return applications;
        }
        public void setApplications(List<CourseMentor> applications) {
            this.applications = applications;
        }


        @Override
        public String toString() {
            return "Mentor{" +
                    "isAvailable=" + isAvailable +
                    ", totalSessions=" + totalSessions +
                    ", expertise='" + expertise + '\'' +
                    ", rating=" + rating +
                    '}';
        }
    }
