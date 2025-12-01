package com.taskflow.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "projects")
public class Project {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
    @Column(nullable = false)
    private String name;
    private String description;
    
    @Column(nullable = false)
    @Builder.Default
    private String background = "linear-gradient(135deg, #0052cc 0%, #2684ff 100%)";
    
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "owner_id", nullable = false) 
    @ToString.Exclude 
    @EqualsAndHashCode.Exclude 
    private User owner;
    @OneToMany(
            mappedBy = "project", 
            cascade = CascadeType.ALL,
            orphanRemoval = true
        )
        @ToString.Exclude
        @EqualsAndHashCode.Exclude
        @Builder.Default
        private List<Task> tasks = new ArrayList<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "pro_members", 
        joinColumns = @JoinColumn(name = "project_id"), 
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<User> members = new HashSet<>();
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Milestone> milestones = new ArrayList<>();
}
