package com.example.affixapp.Person;

public class User extends Person{

    private String profilePhoto;
    String UserId;
    private int age;
    private String zodiacSign;
    // {aries, taurus, gemini, cancer, leo, virgo, libra, scorpio, aquarius, sagittarius, capricorn, pisces}
    private String gender;
    // {male, female, other}
    private String hobby;
    // { sports, travel, books, gym, games, movies, music, other}
    private String profession;
    // { computer, engineering, arts, architecture, math, science, psychology, languages, business, other}
    private String fears;
    // {insects, heights, darkness, dentists, crowds, thunder, other }
    public User(String username, String password, String email) {
        super(username, password, email);
    }

    public User() {
        super();
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getZodiacSign() {
        return zodiacSign;
    }
    public void setZodiacSign(String zodiacSign) {
        this.zodiacSign = zodiacSign;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getFears() {
        return fears;
    }

    public void setFears(String fears) {
        this.fears = fears;
    }
}
