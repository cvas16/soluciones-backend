package com.taskflow.features.tasks.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "dependencies", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"blocked_task_id", "blocker_task_id"})
})
public class Dependency {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_task_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Task blockedTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_task_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Task blockerTask;
}
